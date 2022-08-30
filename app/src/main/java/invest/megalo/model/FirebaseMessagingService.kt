package invest.megalo.model

import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Session(this).deviceToken(p0)
    }
}