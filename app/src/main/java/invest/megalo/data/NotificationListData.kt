package invest.megalo.data

class NotificationListData(
    val notificationId: String,
    val senderUserId: String,
    val receiverUserId: String,
    val title: String,
    val body: String,
    val redirectionPage: String,
    val redirectionPageId: String,
    val seen: Boolean,
    val tappable: Boolean,
    val tapped: Boolean,
    val period: String
)