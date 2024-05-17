import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text


@Composable
fun PermissionDialog(
    permissionTP: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOKClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
    ){
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {Column(modifier=Modifier.fillMaxWidth()){
            Divider()
            Text(
                text = if (isPermanentlyDeclined) "Grant permission" else "OK",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onSettingsClick()
                        } else {
                            onOKClick()
                        }
                    }
                    .padding(16.dp)
            )

        } },
        title = {
                Text(text = "Permission required")
        },
        text = {
            Text(text=
            permissionTP.getDescription(isPermanentlyDeclined)
            )
        },
        modifier = modifier

    )

}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean) : String
}

class NotificationPermissionTextProvider : PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "Please allow notification permission thru settings"
        }else {
            "This apps needs notification permission to earn more respect"
        }
    }

}

class ForegroundPermissionTextProvider : PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "Please allow foreground services thru settings it will allow you to have good time with notifciation"
        }else {
            "This apps needs foreground services  permission to provide intime notification"
        }
    }

}