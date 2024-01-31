package au.com.shiftyjelly.pocketcasts.player.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.SimpleItemAnimator
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsEvent
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsTrackerWrapper
import au.com.shiftyjelly.pocketcasts.models.to.Chapter
import au.com.shiftyjelly.pocketcasts.player.databinding.FragmentChaptersBinding
import au.com.shiftyjelly.pocketcasts.player.viewmodel.PlayerViewModel
import au.com.shiftyjelly.pocketcasts.views.fragments.BaseFragment
import au.com.shiftyjelly.pocketcasts.views.helper.UiUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import au.com.shiftyjelly.pocketcasts.localization.R as LR

@AndroidEntryPoint
class ChaptersFragment : BaseFragment(), ChapterListener {

    @Inject lateinit var analyticsTracker: AnalyticsTrackerWrapper
    private val playerViewModel: PlayerViewModel by activityViewModels()
    lateinit var adapter: ChapterAdapter
    private var binding: FragmentChaptersBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChaptersBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = ChapterAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (binding?.recyclerView?.itemAnimator as SimpleItemAnimator).changeDuration = 0
        binding?.recyclerView?.adapter = adapter
        playerViewModel.listDataLive.observe(viewLifecycleOwner) {
            adapter.submitList(it.chapters)
            view.setBackgroundColor(it.podcastHeader.backgroundColor)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                playerViewModel.showPlayerFlow.collect { showPlayer() }
            }
        }
    }

    fun scrollToChapter(chapter: Chapter) {
        playerViewModel.listDataLive.value?.chapters?.indexOf(chapter)?.let { position ->
            binding?.recyclerView?.scrollToPosition(position)
        }
    }

    override fun onChapterClick(chapter: Chapter) {
        analyticsTracker.track(AnalyticsEvent.PLAYER_CHAPTER_SELECTED)
        playerViewModel.onChapterClick(chapter)
    }

    override fun onChapterUrlClick(chapter: Chapter) {
        chapter.url?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(it.toString())
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                UiUtil.displayAlertError(requireContext(), getString(LR.string.player_open_url_failed, it), null)
            }
        }
    }

    private fun showPlayer() {
        (parentFragment as? PlayerContainerFragment)?.openPlayer()
    }
}
