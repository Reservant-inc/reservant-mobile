package reservant_mobile

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.util.DebugLogger

class ApplicationService: Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ApplicationService
            private set
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.1)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }
}