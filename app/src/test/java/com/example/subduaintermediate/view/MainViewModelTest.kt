import io.mockk.coEvery
import io.mockk.mockk
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.example.subduaintermediate.data.response.ListStoryItem
import com.example.subduaintermediate.view.main.MainViewModel
import com.example.subduaintermediate.repository.StoryRepository
import com.example.subduaintermediate.repository.UserRepository
import com.example.subduaintermediate.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.paging.AsyncPagingDataDiffer
import com.example.subduaintermediate.DataDummy
import com.example.subduaintermediate.MainDispatcherRule
import com.example.subduaintermediate.view.adaptor.MainListAdaptor

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = mockk<UserRepository>()
    private val storyRepository = mockk<StoryRepository>()

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = PagingData.from(dummyStory)

        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        coEvery { storyRepository.getStory() } returns expectedStory

        val mainViewModel = MainViewModel(userRepository, storyRepository)

        val actualStory: PagingData<ListStoryItem> = mainViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainListAdaptor.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        coEvery { storyRepository.getStory() } returns expectedStory

        val mainViewModel = MainViewModel(userRepository, storyRepository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainListAdaptor.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }
}
