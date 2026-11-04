package com.example.data.parser

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

data class ParsedQuickAddResult(
    val entityType: String, // TASK, PAYMENT, MEETING, EVENT, TRIP, NOTE
    val title: String,
    val date: String, // YYYY-MM-DD
    val time: String? = null, // HH:mm
    val amount: Double? = null,
    val category: String = "Personal",
    val rawText: String
)

object QuickAddNlpParser {

    private val amountPattern = Pattern.compile("(?:rs\\.?|inr|₹|amount|usd|\\$)?\\s*([0-9]+(?:\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE)
    private val timePattern = Pattern.compile("\\b(?:at\\s+)?([0-1]?[0-9]|2[0-3])(?::([0-5][0-9]))?\\s*(am|pm)?\\b", Pattern.CASE_INSENSITIVE)
    private val monthDayPattern = Pattern.compile("\\b(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)\\s+([0-3]?[0-9])(?:st|nd|rd|th)?\\b", Pattern.CASE_INSENSITIVE)
    private val dayMonthPattern = Pattern.compile("\\b([0-3]?[0-9])(?:st|nd|rd|th)?\\s+(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)\\b", Pattern.CASE_INSENSITIVE)

    fun parse(input: String): ParsedQuickAddResult {
        val trimmed = input.trim()
        val lower = trimmed.lowercase(Locale.ROOT)
        val cal = Calendar.getInstance()
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        var detectedDate = sdfDate.format(cal.time)
        var detectedTime: String? = null
        var detectedAmount: Double? = null

        // 1. Date Detection
        if (lower.contains("tomorrow") && !lower.contains("day after tomorrow")) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            detectedDate = sdfDate.format(cal.time)
        } else if (lower.contains("day after tomorrow")) {
            cal.add(Calendar.DAY_OF_YEAR, 2)
            detectedDate = sdfDate.format(cal.time)
        } else if (lower.contains("next week")) {
            cal.add(Calendar.DAY_OF_YEAR, 7)
            detectedDate = sdfDate.format(cal.time)
        } else {
            // Check month names like "November 5" or "5 November"
            val m1 = monthDayPattern.matcher(trimmed)
            if (m1.find()) {
                val monthStr = m1.group(1)
                val dayStr = m1.group(2)
                val monthIndex = parseMonth(monthStr)
                val day = dayStr.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
                val targetCal = Calendar.getInstance()
                targetCal.set(Calendar.MONTH, monthIndex)
                targetCal.set(Calendar.DAY_OF_MONTH, day)
                if (targetCal.before(cal)) {
                    targetCal.add(Calendar.YEAR, 1)
                }
                detectedDate = sdfDate.format(targetCal.time)
            } else {
                val m2 = dayMonthPattern.matcher(trimmed)
                if (m2.find()) {
                    val dayStr = m2.group(1)
                    val monthStr = m2.group(2)
                    val monthIndex = parseMonth(monthStr)
                    val day = dayStr.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
                    val targetCal = Calendar.getInstance()
                    targetCal.set(Calendar.MONTH, monthIndex)
                    targetCal.set(Calendar.DAY_OF_MONTH, day)
                    if (targetCal.before(cal)) {
                        targetCal.add(Calendar.YEAR, 1)
                    }
                    detectedDate = sdfDate.format(targetCal.time)
                }
            }
        }

        // 2. Time Detection
        val timeMatcher = timePattern.matcher(trimmed)
        while (timeMatcher.find()) {
            val hourStr = timeMatcher.group(1)
            val minStr = timeMatcher.group(2)
            val ampm = timeMatcher.group(3)?.lowercase(Locale.ROOT)
            val h = hourStr.toIntOrNull()
            if (h != null && (ampm != null || trimmed.substring(timeMatcher.start()).contains("at") || (minStr != null))) {
                var hour = h
                val min = minStr?.toIntOrNull() ?: 0
                if (ampm == "pm" && hour < 12) hour += 12
                if (ampm == "am" && hour == 12) hour = 0
                detectedTime = String.format(Locale.US, "%02d:%02d", hour, min)
                break
            }
        }

        // 3. Amount Detection (especially for payments)
        val isPaymentContext = lower.contains("pay") || lower.contains("rent") || lower.contains("bill") ||
                lower.contains("emi") || lower.contains("recharge") || lower.contains("credit card") ||
                lower.contains("electricity") || lower.contains("fees") || lower.contains("₹") || lower.contains("rs")

        if (isPaymentContext) {
            val amtMatcher = amountPattern.matcher(trimmed)
            while (amtMatcher.find()) {
                val amt = amtMatcher.group(1)?.toDoubleOrNull()
                if (amt != null && amt > 0) {
                    detectedAmount = amt
                    break
                }
            }
        }

        // 4. Entity Type Classification
        val entityType: String
        val category: String
        when {
            isPaymentContext -> {
                entityType = "PAYMENT"
                category = when {
                    lower.contains("rent") -> "Rent"
                    lower.contains("electricity") -> "Electricity"
                    lower.contains("water") -> "Water"
                    lower.contains("recharge") || lower.contains("mobile") -> "Mobile Recharge"
                    lower.contains("credit card") -> "Credit Card"
                    lower.contains("emi") -> "EMI"
                    else -> "Finance"
                }
            }
            lower.contains("meeting") || lower.contains("client") || lower.contains("standup") ||
                    lower.contains("sync with") || lower.contains("discussion") || lower.contains("presentation") -> {
                entityType = "MEETING"
                category = "Work"
            }
            lower.contains("trip to") || lower.contains("flight to") || lower.contains("vacation") ||
                    lower.contains("travel to") || lower.contains("hotel") -> {
                entityType = "TRIP"
                category = "Travel"
            }
            lower.contains("doctor") || lower.contains("dentist") || lower.contains("appointment") ||
                    lower.contains("lunch with") || lower.contains("dinner with") || lower.contains("party") ||
                    lower.contains("birthday") -> {
                entityType = "EVENT"
                category = if (lower.contains("lunch") || lower.contains("dinner") || lower.contains("birthday")) "Personal" else "Personal"
            }
            lower.contains("note:") || lower.contains("remember:") || lower.contains("idea:") -> {
                entityType = "NOTE"
                category = "Personal"
            }
            else -> {
                entityType = "TASK"
                category = if (lower.contains("report") || lower.contains("office") || lower.contains("client") || lower.contains("work")) "Work" else "Personal"
            }
        }

        // 5. Clean Title Extraction
        var cleanTitle = trimmed
        // Strip common prefix prepositions or commands
        val cleanRegex = "(?i)\\b(on|at|by|due|for|rs\\.?|inr|₹|\\$)\\b.*$"
        if (cleanTitle.contains(Regex(cleanRegex))) {
            cleanTitle = cleanTitle.replace(Regex(cleanRegex), "").trim()
        }
        if (cleanTitle.length < 3) cleanTitle = trimmed

        // Capitalize first letter
        cleanTitle = cleanTitle.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        return ParsedQuickAddResult(
            entityType = entityType,
            title = cleanTitle,
            date = detectedDate,
            time = detectedTime ?: if (entityType == "MEETING") "10:00" else null,
            amount = detectedAmount,
            category = category,
            rawText = input
        )
    }

    private fun parseMonth(monthStr: String): Int {
        val m = monthStr.lowercase(Locale.ROOT)
        return when {
            m.startsWith("jan") -> Calendar.JANUARY
            m.startsWith("feb") -> Calendar.FEBRUARY
            m.startsWith("mar") -> Calendar.MARCH
            m.startsWith("apr") -> Calendar.APRIL
            m.startsWith("may") -> Calendar.MAY
            m.startsWith("jun") -> Calendar.JUNE
            m.startsWith("jul") -> Calendar.JULY
            m.startsWith("aug") -> Calendar.AUGUST
            m.startsWith("sep") -> Calendar.SEPTEMBER
            m.startsWith("oct") -> Calendar.OCTOBER
            m.startsWith("nov") -> Calendar.NOVEMBER
            m.startsWith("dec") -> Calendar.DECEMBER
            else -> Calendar.getInstance().get(Calendar.MONTH)
        }
    }
}
