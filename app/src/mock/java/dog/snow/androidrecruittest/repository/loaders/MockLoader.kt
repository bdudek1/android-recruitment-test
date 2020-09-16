package dog.snow.androidrecruittest.repository.loaders

import java.io.IOException
import java.io.InputStream

class MockLoader private constructor() {
    fun loadJson(assetName: String): String {
        return try {
            loadJsonFromResource(assetName)
        } catch (e: IOException) {
            println("MockLoader exception: $e")
        }
    }

    @Throws(IOException::class)
    private fun loadJsonFromResource(assetName: String): String {
        return loadJsonInputStream(javaClass.classLoader?.getResourceAsStream(assetName))
    }

    @Throws(IOException::class)
    private fun loadJsonInputStream(inputStream: InputStream?): String {
        if (inputStream == null) return ""
        return inputStream.bufferedReader().use { it.readText() }
    }

    companion object {
        @JvmStatic
        fun createResourceLoader(): MockLoader {
            return MockLoader()
        }
    }
}