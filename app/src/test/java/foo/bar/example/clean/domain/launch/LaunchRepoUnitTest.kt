package foo.bar.example.clean.domain.launch

import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.Either
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.delegate.TestDelegateDefault
import foo.bar.example.clean.domain.ErrorResolution
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test


/**
 * Tests for this repo cover a few areas:
 *
 * 1) Construction: we check that the repo is constructed in the correct state
 * 2) Receiving data: we check that the repo behaves appropriately when receiving various success and fail responses from the Service
 * 3) Observers and State: we check that the repo updates its observers correctly and presents its current state accurately
 *
 */
class LaunchRepoUnitTest {

    private val launch = Launch("123", "site", true, "http://www.test.com/someimage.png")

    @MockK
    private lateinit var mockSuccess: Success

    @MockK
    private lateinit var mockFailureWithPayload: FailureWithPayload<ErrorResolution>

    @MockK
    private lateinit var mockLaunchService: LaunchService

    @MockK
    private lateinit var mockObserver: Observer


    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        ForeDelegateHolder.setDelegate(TestDelegateDefault())
    }


    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange
        val launchesRepo = LaunchRepo(
                mockLaunchService,
                Dispatchers.Unconfined,
                logger
        )

        //act

        //assert
        Assert.assertEquals(false, launchesRepo.currentState.isUpdating)
        Assert.assertEquals(NO_ID, launchesRepo.currentState.launch.id)
        Assert.assertEquals(false, launchesRepo.currentState.launch.isBooked)
    }


    @Test
    @Throws(Exception::class)
    fun fetchLaunch_MockSuccess() {

        //arrange
        coEvery {
            mockLaunchService.getLaunchList()
        } returns Either.right(listOf(launch))

        val launchesRepo = LaunchRepo(
            mockLaunchService,
            Dispatchers.Unconfined,
            logger
        )

        //act
        launchesRepo.fetchLaunch(mockSuccess, mockFailureWithPayload)

        //assert
        verify(exactly = 1) {
            mockSuccess()
        }
        verify(exactly = 0) {
            mockFailureWithPayload(any())
        }
        Assert.assertEquals(false, launchesRepo.currentState.isUpdating)
        Assert.assertEquals(launch.site, launchesRepo.currentState.launch.site)
        Assert.assertEquals(launch.isBooked, launchesRepo.currentState.launch.isBooked)
        Assert.assertEquals(launch.id, launchesRepo.currentState.launch.id)
    }


    @Test
    @Throws(Exception::class)
    fun fetchLaunch_MockFailure() {

        //arrange
        coEvery {
            mockLaunchService.getLaunchList()
        } returns Either.left(ErrorResolution.RETRY_LATER)

        val launchesRepo = LaunchRepo(
            mockLaunchService,
            Dispatchers.Unconfined,
            logger
        )

        //act
        launchesRepo.fetchLaunch(mockSuccess, mockFailureWithPayload)

        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(ErrorResolution.RETRY_LATER))
        }
        Assert.assertEquals(false, launchesRepo.currentState.isUpdating)
        Assert.assertEquals(false, launchesRepo.currentState.launch.isBooked)
        Assert.assertEquals(NO_ID, launchesRepo.currentState.launch.id)
    }


    /**
     *
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times this method might be called. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times the observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * See the databinding docs for more information about this
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnce() {

        //arrange
        coEvery {
            mockLaunchService.getLaunchList()
        } returns Either.right(listOf(launch))

        val launchesRepo = LaunchRepo(
            mockLaunchService,
            Dispatchers.Unconfined,
            logger
        )
        launchesRepo.addObserver(mockObserver)

        //act
        launchesRepo.fetchLaunch(mockSuccess, mockFailureWithPayload)

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    companion object {
        private val logger = SystemLogger()
    }
}
