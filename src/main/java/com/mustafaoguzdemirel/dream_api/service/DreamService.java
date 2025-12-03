package com.mustafaoguzdemirel.dream_api.service;

import com.mustafaoguzdemirel.dream_api.dto.request.DreamSaveRequest;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamCalendarResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamDetailResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamListItemResponse;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.exception.DreamNotFoundException;
import com.mustafaoguzdemirel.dream_api.exception.UserNotFoundException;
import com.mustafaoguzdemirel.dream_api.repository.DreamRepository;
import com.mustafaoguzdemirel.dream_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for dream CRUD operations and interpretation coordination
 * Delegates AI operations to OpenAiService and user profiling to UserProfileService
 */
@Service
public class DreamService {

    private final UserRepository userRepository;
    private final DreamRepository dreamRepository;
    private final OpenAiService openAiService;
    private final UserProfileService userProfileService;

    public DreamService(
            UserRepository userRepository,
            DreamRepository dreamRepository,
            OpenAiService openAiService,
            UserProfileService userProfileService
    ) {
        this.userRepository = userRepository;
        this.dreamRepository = dreamRepository;
        this.openAiService = openAiService;
        this.userProfileService = userProfileService;
    }

    /**
     * Saves a dream to the database
     */
    public Dream saveDream(DreamSaveRequest request) {
        AppUser user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        Dream dream = new Dream();
        dream.setUser(user);
        dream.setDreamText(request.getDreamText());
        dream.setInterpretation(request.getInterpretation());
        dream.setCreatedAt(LocalDateTime.now());

        return dreamRepository.save(dream);
    }

    /**
     * Gets all dreams for a user
     */
    public List<Dream> getDreamHistory(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return dreamRepository.findByUser(user);
    }

    /**
     * Generates a calendar view of user's dreams
     */
    public DreamCalendarResponse getDreamCalendar(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Dream> dreams = dreamRepository.findByUser(user);

        int currentYear = LocalDate.now().getYear();
        List<DreamCalendarResponse.MonthData> monthList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            int daysInMonth = yearMonth.lengthOfMonth();

            LocalDate firstDayOfMonth = LocalDate.of(currentYear, month, 1);
            DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();
            int startOffset = firstDayOfWeek.getValue() - 1;

            List<DreamCalendarResponse.DayData> days = new ArrayList<>();

            // Add previous month days
            YearMonth prevMonth = yearMonth.minusMonths(1);
            int prevMonthDays = prevMonth.lengthOfMonth();
            for (int i = startOffset - 1; i >= 0; i--) {
                int dayNum = prevMonthDays - i;
                LocalDate date = LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), dayNum);
                UUID dreamId = findDreamIdByDate(dreams, date);
                days.add(new DreamCalendarResponse.DayData(dayNum, dreamId, false));
            }

            // Add current month days
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = LocalDate.of(currentYear, month, day);
                UUID dreamId = findDreamIdByDate(dreams, date);
                days.add(new DreamCalendarResponse.DayData(day, dreamId, true));
            }

            // Add next month days to fill 35 slots
            int remaining = 35 - days.size();
            YearMonth nextMonth = yearMonth.plusMonths(1);
            for (int i = 1; i <= remaining; i++) {
                LocalDate date = LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), i);
                UUID dreamId = findDreamIdByDate(dreams, date);
                days.add(new DreamCalendarResponse.DayData(i, dreamId, false));
            }

            monthList.add(new DreamCalendarResponse.MonthData(month, days));
        }

        return new DreamCalendarResponse(currentYear, monthList);
    }

    private UUID findDreamIdByDate(List<Dream> dreams, LocalDate date) {
        return dreams.stream()
                .filter(d -> d.getCreatedAt().toLocalDate().isEqual(date))
                .map(Dream::getId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets dream detail by ID
     */
    public DreamDetailResponse getDreamDetail(UUID dreamId) {
        Dream dream = dreamRepository.findById(dreamId)
                .orElseThrow(() -> new DreamNotFoundException(dreamId));

        return new DreamDetailResponse(
                dream.getId(),
                dream.getDreamText(),
                dream.getInterpretation(),
                dream.getDetailedInterpretation(),
                dream.getCreatedAt()
        );
    }

    /**
     * Interprets a dream for a user using AI
     * Coordinates between user profile service and OpenAI service
     */
    public DreamDetailResponse interpretDreamForUser(UUID userId, String dreamText) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        LocalDate today = LocalDate.now();

        // TODO: Uncomment to enable daily limit
        // if (today.equals(user.getLastDreamInterpretedDate())) {
        //     throw new RuntimeException("User has already interpreted a dream today.");
        // }

        // Build personalized prompt using user profile
        String userProfilePrompt = userProfileService.buildUserProfilePrompt(userId);

        String fullPrompt = "Give a short and emotionally warm interpretation of this dream " +
                userProfilePrompt + ". Write naturally, like giving comforting advice, but keep it under 150 words. Dream text: " + dreamText;

        // Get interpretation from OpenAI
        String interpretation = openAiService.generateCompletion(fullPrompt);

        // Save dream
        Dream dream = new Dream();
        dream.setUser(user);
        dream.setDreamText(dreamText);
        dream.setInterpretation(interpretation);
        dream.setCreatedAt(LocalDateTime.now());

        dreamRepository.save(dream);

        // Update last interpretation date
        user.setLastDreamInterpretedDate(today);
        userRepository.save(user);

        return new DreamDetailResponse(
                dream.getId(),
                dream.getDreamText(),
                interpretation,
                "",
                dream.getCreatedAt()
        );
    }

    /**
     * Generates a detailed interpretation for an existing dream
     */
    public DreamDetailResponse getDetailedInterpretation(UUID dreamId) {
        Dream dream = dreamRepository.findById(dreamId)
                .orElseThrow(() -> new DreamNotFoundException(dreamId));

        AppUser user = dream.getUser();
        String userProfilePrompt = userProfileService.buildUserProfilePrompt(user.getUserId());

        String fullPrompt = "You are an experienced dream analyst. Provide a detailed and insightful interpretation " +
                "of the following dream, taking into account the user's personality and emotional profile " + userProfilePrompt +
                ". Expand upon the short interpretation provided below, adding psychological, emotional, and symbolic analysis. " +
                "Write in a compassionate and inspiring tone, around 300–500 words. " +
                "\n\nDream text: " + dream.getDreamText() +
                "\n\nShort interpretation: " + dream.getInterpretation();

        String detailedInterpretation = openAiService.generateCompletion(fullPrompt);

        // Update dream record
        dream.setDetailedInterpretation(detailedInterpretation);
        dreamRepository.save(dream);

        return new DreamDetailResponse(
                dream.getId(),
                dream.getDreamText(),
                dream.getInterpretation(),
                detailedInterpretation,
                dream.getCreatedAt()
        );
    }

    /**
     * Gets dream list for a user
     */
    public List<DreamListItemResponse> getDreamList(UUID userId, boolean isLastThree) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Dream> dreams;
        if (isLastThree) {
            dreams = dreamRepository.findTop3ByUserOrderByCreatedAtDesc(user);
        } else {
            dreams = dreamRepository.findByUserOrderByCreatedAtDesc(user);
        }

        return dreams.stream()
                .map(dream -> new DreamListItemResponse(
                        dream.getCreatedAt().getDayOfMonth(),
                        dream.getCreatedAt().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        dream.getDreamText(),
                        dream.getId()
                ))
                .collect(Collectors.toList());
    }
}