package au.com.shiftyjelly.pocketcasts.repositories.endofyear.stories

import au.com.shiftyjelly.pocketcasts.models.db.helper.TopPodcast

class StoryTopPodcast(
    val topPodcast: TopPodcast,
) : Story() {
    override val identifier: String = "top_one_podcast"
}
