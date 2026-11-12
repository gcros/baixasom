onClick = {
    val candidate = link.trim()

    if (candidate.isBlank()) {
        status = "Cole um link válido."
        return@Button
    }

    runCatching {
        val uri = Uri.parse(candidate)

        val name = uri.lastPathSegment
            ?.substringAfterLast('/')
            ?.ifBlank { "arquivo" }
            ?: "arquivo"

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

        val manager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        manager.enqueue(request)

        status = "Download iniciado! Acompanhe o progresso nas notificações."
    }.onFailure {
        status = "Não foi possível iniciar o download."
    }
}
