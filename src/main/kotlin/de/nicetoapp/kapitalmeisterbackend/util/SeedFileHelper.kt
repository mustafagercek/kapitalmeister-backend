package de.nicetoapp.kapitalmeisterbackend.util

data class Range(val start: Int, val end: Int)

class SeedFileHelper {

    fun findMissingRanges(fileNames: List<String>): List<Range> {
        val yearRanges = fileNames.mapNotNull { parseRangeFromFilename(it) }
        val sortedRanges = yearRanges.sortedBy { it.start }

        val missingRanges = mutableListOf<Range>()
        for (i in 0 until sortedRanges.size - 1) {
            val current = sortedRanges[i]
            val next = sortedRanges[i + 1]
            if (current.end + 1 < next.start) { // Check if there's a gap
                missingRanges.add(Range(current.end + 1, next.start - 1))
            }
        }
        return missingRanges
    }

    private fun parseRangeFromFilename(filename: String): Range? {
        // Regular expression to match year ranges in the filename
        val regex = """(\d{4})[-_](\d{4})""".toRegex()
        // Parse the range using the regex
        return regex.find(filename)?.destructured?.let { (start, end) ->
            Range(start.toInt(), end.toInt())
        }
    }
}
