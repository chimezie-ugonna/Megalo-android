package invest.megalo.model

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import invest.megalo.R
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class KeyStore(private val context: Context) {
    private val alias = "${context.resources.getString(R.string.app_name)} Key"
    private var keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")

    init {
        keyStore.load(null)
    }

    private fun generateKey(): SecretKey {
        if (keyStore.containsAlias(alias)) {
            deleteKey()
        }
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            build()
        }

        keyGenerator.init(keyGenParameterSpec)

        return keyGenerator.generateKey()
    }

    private fun getKey(): SecretKey {
        val keyStoreSecretKeyEntry = keyStore.getEntry(
            alias, null
        ) as KeyStore.SecretKeyEntry
        return keyStoreSecretKeyEntry.secretKey
    }

    fun deleteKey() {
        keyStore.deleteEntry(alias)
        Session(context).encryptedTokenIv(
            ""
        )
        Session(context).encryptedToken(
            ""
        )
    }

    fun encryptData(data: String) {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        var temp = data
        while (temp.toByteArray().size % 16 != 0) {
            temp += "\u0020"
        }
        cipher.init(Cipher.ENCRYPT_MODE, generateKey())
        val ivBytes = cipher.iv
        val encryptedBytes = cipher.doFinal(temp.toByteArray(Charsets.UTF_8))
        Session(context).encryptedTokenIv(
            Base64.encodeToString(ivBytes, Base64.NO_WRAP)
        )
        Session(context).encryptedToken(
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        )
    }

    fun decryptData(
        ivBytes: ByteArray = Base64.decode(Session(context).encryptedTokenIv(), Base64.NO_WRAP),
        data: ByteArray = Base64.decode(Session(context).encryptedToken(), Base64.NO_WRAP)
    ): String {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val spec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        return cipher.doFinal(data).toString(Charsets.UTF_8).trim()
    }
}