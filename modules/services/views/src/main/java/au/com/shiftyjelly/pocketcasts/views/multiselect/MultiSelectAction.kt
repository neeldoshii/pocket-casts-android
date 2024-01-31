package au.com.shiftyjelly.pocketcasts.views.multiselect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.models.entity.BaseEpisode
import au.com.shiftyjelly.pocketcasts.models.entity.PodcastEpisode
import au.com.shiftyjelly.pocketcasts.models.entity.UserEpisode
import au.com.shiftyjelly.pocketcasts.views.R
import au.com.shiftyjelly.pocketcasts.images.R as IR
import au.com.shiftyjelly.pocketcasts.localization.R as LR
import au.com.shiftyjelly.pocketcasts.ui.R as UR

sealed class MultiSelectAction(
    val groupId: Int,
    val actionId: Int,
    @StringRes val title: Int,
    @DrawableRes val iconRes: Int,
    val analyticsValue: String,
) {
    object DeleteDownload : MultiSelectAction(
        R.id.menu_download,
        UR.id.menu_undownload,
        LR.string.delete_download,
        IR.drawable.ic_undownload,
        "remove_download"
    )
    object Download : MultiSelectAction(
        R.id.menu_download,
        R.id.menu_download,
        LR.string.download,
        IR.drawable.ic_download,
        "download"
    )

    object Archive : MultiSelectAction(
        R.id.menu_archive,
        R.id.menu_archive,
        LR.string.archive,
        IR.drawable.ic_archive,
        "archive"
    )
    object Unarchive : MultiSelectAction(
        R.id.menu_archive,
        UR.id.menu_unarchive,
        LR.string.unarchive,
        IR.drawable.ic_unarchive,
        "unarchive"
    )
    object DeleteUserEpisode : MultiSelectAction(
        R.id.menu_archive,
        R.id.menu_delete,
        LR.string.delete,
        R.drawable.ic_delete,
        "delete"
    )

    object MarkAsUnplayed : MultiSelectAction(
        R.id.menu_mark_played,
        UR.id.menu_markasunplayed,
        LR.string.mark_as_unplayed,
        IR.drawable.ic_markasunplayed,
        "mark_as_unplayed"
    )
    object MarkAsPlayed : MultiSelectAction(
        R.id.menu_mark_played,
        R.id.menu_mark_played,
        LR.string.mark_as_played,
        IR.drawable.ic_markasplayed,
        "mark_as_played"
    )

    object PlayNext : MultiSelectAction(
        R.id.menu_playnext,
        R.id.menu_playnext,
        LR.string.play_next,
        IR.drawable.ic_upnext_playnext,
        "play_next"
    )

    object PlayLast : MultiSelectAction(
        R.id.menu_playlast,
        R.id.menu_playlast,
        LR.string.play_last,
        IR.drawable.ic_upnext_playlast,
        "play_last"
    )

    object Unstar : MultiSelectAction(
        R.id.menu_star,
        UR.id.menu_unstar,
        LR.string.unstar,
        IR.drawable.ic_unstar,
        "unstar"
    )
    object Star : MultiSelectAction(
        R.id.menu_star,
        R.id.menu_star,
        LR.string.star,
        IR.drawable.ic_star,
        "star"
    )

    object SelectAll : MultiSelectAction(
        R.id.menu_select_all,
        R.id.menu_select_all,
        LR.string.select_all,
        IR.drawable.ic_selectall_up,
        "select_all"
    )

    companion object {
        val STANDARD = listOf(Download, Archive, MarkAsPlayed, PlayNext, PlayLast, Star)
        val ALL = STANDARD + listOf(DeleteDownload, DeleteUserEpisode, MarkAsUnplayed, Unstar, Unarchive)
        val STANDARD_BY_ID = STANDARD.associateBy { it.actionId }
        val ALL_BY_ID = ALL.associateBy { it.actionId }

        fun listFromIds(list: List<Int>): List<MultiSelectAction> {
            val loadedItems = list.mapNotNull { STANDARD_BY_ID[it] }
            val missingItems = STANDARD.subtract(loadedItems) // We need to add on any missing items in case we add actions in the future
            return loadedItems + missingItems
        }

        fun actionForGroup(groupId: Int, selected: List<BaseEpisode>): MultiSelectAction? {
            when (groupId) {
                R.id.menu_download -> {
                    for (episode in selected) {
                        if (!episode.isDownloaded) {
                            return Download
                        }
                    }

                    return DeleteDownload
                }
                R.id.menu_archive -> {
                    for (episode in selected) {
                        if (episode is PodcastEpisode && !episode.isArchived) {
                            return Archive
                        }
                    }

                    return if (selected.filterIsInstance<UserEpisode>().size == selected.size) DeleteUserEpisode else Unarchive
                }
                R.id.menu_mark_played -> {
                    for (episode in selected) {
                        if (!episode.isFinished) {
                            return MarkAsPlayed
                        }
                    }

                    return MarkAsUnplayed
                }
                R.id.menu_star -> {
                    if (selected.filterIsInstance<UserEpisode>().isNotEmpty()) return null
                    for (episode in selected) {
                        if (episode is PodcastEpisode && !episode.isStarred) {
                            return Star
                        }
                    }

                    return Unstar
                }
                R.id.menu_playnext -> return PlayNext
                R.id.menu_playlast -> return PlayLast
            }

            return null
        }
    }
}
