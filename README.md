# BaixaSom

Aplicativo Android nativo (Kotlin + Jetpack Compose) para baixar arquivos de áudio por link direto HTTPS quando o usuário tem autorização para salvá-los.

## Recursos iniciais
- Interface escura com detalhes laranja
- Campo para colar link
- Download de arquivos de áudio diretos: MP3, M4A, AAC, WAV, OGG e FLAC
- Download Manager do Android com notificação e salvamento em Música/BaixaSom
- GitHub Actions para gerar APK de teste

## Limites importantes
O app não extrai áudio de páginas do YouTube nem contorna DRM, paywalls ou controles de acesso. Para vídeos do YouTube, use os recursos oficiais de download quando disponíveis. O app aceita URLs diretas de arquivos que você tem direito de baixar.

## Compilar
`gradle assembleDebug`

APK: `app/build/outputs/apk/debug/app-debug.apk`
