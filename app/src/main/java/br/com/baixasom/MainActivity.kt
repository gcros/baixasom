package br.com.baixasom

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF101010)
private val Panel = Color(0xFF1D1D1D)
private val Orange = Color(0xFFFF7A18)
private val Muted = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BaixaSomScreen(this) }
    }
}

@Composable
private fun BaixaSomScreen(context: Context) {
    var link by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Cole um link de áudio direto para começar.") }

    MaterialTheme(colorScheme = darkColorScheme(primary = Orange, background = Ink, surface = Panel)) {
        Surface(Modifier.fillMaxSize(), color = Ink) {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(28.dp))
                Box(Modifier.size(82.dp).background(Orange, RoundedCornerShape(26.dp)), Alignment.Center) {
                    Icon(Icons.Default.MusicNote, null, tint = Ink, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(18.dp))
                Text("BaixaSom", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("SEU ÁUDIO, DO SEU JEITO", color = Orange, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(42.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("LINK DO ÁUDIO", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = link,
                            onValueChange = { link = it; status = "Pronto para verificar o link." },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("https://site.com/audio.mp3", color = Muted) },
                            leadingIcon = { Icon(Icons.Default.Link, null, tint = Orange) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Orange, unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val candidate = link.trim()
                                val uri = runCatching { Uri.parse(candidate) }.getOrNull()
                                val host = uri?.host?.lowercase().orEmpty()

                                if (candidate.isBlank()) {
                                    status = "Cole um link válido."
                                } else if (host == "youtube.com" || host.endsWith(".youtube.com") ||
                                    host == "youtu.be" || host.endsWith(".youtu.be")) {
                                    status = "Links do YouTube não podem ser extraídos por este app. Use o download oficial quando disponível ou um arquivo que você tenha autorização para baixar."
                                } else if (uri == null || uri.scheme != "https" || host.isBlank()) {
                                    status = "Cole um link HTTPS válido de um arquivo de áudio autorizado."
                                } else if (!candidate.substringBefore('?').lowercase()
                                        .matches(Regex(".*\\.(mp3|m4a|aac|wav|ogg|flac)$"))) {
                                    status = "Este link não parece ser um arquivo de áudio direto (MP3, M4A, AAC, WAV, OGG ou FLAC)."
                                } else {
                                    runCatching {
                                        val name = uri.lastPathSegment
                                            ?.substringAfterLast('/')
                                            ?.takeIf { it.isNotBlank() }
                                            ?.replace(Regex("[^A-Za-z0-9._-]"), "_")
                                            ?: "audio.mp3"

                                        val request = DownloadManager.Request(uri)
                                            .setTitle(name)
                                            .setDescription("Baixando com BaixaSom")
                                            .setNotificationVisibility(
                                                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                                            )
                                            .setDestinationInExternalPublicDir(
                                                Environment.DIRECTORY_DOWNLOADS,
                                                "BaixaSom/$name"
                                            )

                                        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                        manager.enqueue(request)
                                        status = "Download iniciado! Acompanhe o progresso nas notificações."
                                    }.onFailure {
                                        status = "Não foi possível iniciar o download."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Ink)
                        ) {
                            Icon(Icons.Default.Download, null)
                            Spacer(Modifier.width(9.dp))
                            Text("BAIXAR ÁUDIO", fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text(status, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.weight(1f))
                Text("Baixe somente arquivos que você possui ou tem autorização para salvar.", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}
