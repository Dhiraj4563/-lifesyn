package com.example.data.model

enum class TaskCategory(val displayName: String) {
    WORK("Work"),
    PERSONAL("Personal"),
    FAMILY("Family"),
    FRIENDS("Friends"),
    FINANCE("Finance"),
    TRAVEL("Travel"),
    SHOPPING("Shopping"),
    OTHER("Other")
}

enum class Priority(val displayName: String) {
    URGENT("Urgent"),
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}

enum class TaskStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class PaymentCategory(val displayName: String) {
    EMI("EMI / Loan"),
    RENT("Rent"),
    ELECTRICITY("Electricity"),
    WATER("Water"),
    INTERNET("Internet / Wifi"),
    MOBILE("Mobile Recharge"),
    CREDIT_CARD("Credit Card"),
    INSURANCE("Insurance"),
    SUBSCRIPTION("Subscription"),
    LOAN("Personal Loan"),
    LENT("Money Lent"),
    BORROWED("Money Borrowed"),
    OTHER("Other")
}

enum class PaymentStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    DUE_TODAY("Due Today"),
    OVERDUE("Overdue"),
    PAID("Paid"),
    CANCELLED("Cancelled")
}

enum class TripStatus(val displayName: String) {
    IDEA("Idea"),
    PLANNING("Planning"),
    BOOKED("Booked"),
    CONFIRMED("Confirmed"),
    UPCOMING("Upcoming"),
    ONGOING("Ongoing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class ContactCategory(val displayName: String) {
    FAMILY("Family"),
    FRIENDS("Friends"),
    RELATIVES("Relatives"),
    COLLEAGUES("Colleagues"),
    OTHER("Other")
}

enum class GoalType(val displayName: String) {
    DAILY("Daily Goal"),
    WEEKLY("Weekly Goal"),
    MONTHLY("Monthly Goal"),
    HABIT("Habit Routine")
}

enum class Recurrence(val displayName: String) {
    NONE("One-time"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

enum class CalendarViewMode(val displayName: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    AGENDA("Agenda")
}
