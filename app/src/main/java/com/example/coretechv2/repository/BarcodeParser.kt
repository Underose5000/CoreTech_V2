package com.example.coretechv2.repository

/**
 * The useful parts of a scanned barcode.
 *
 * @property raw The scanned text, with any symbology prefix removed.
 * @property identifiers Values that may identify the item, in order of
 * preference: the GTIN from a GS1 barcode, then the raw text.
 * @property batch The batch or lot number (GS1 AI 10), if present.
 * @property labelQty The count on the label (GS1 AI 37 or 30), if present.
 */
data class ScannedBarcode(
    val raw: String,
    val identifiers: List<String>,
    val batch: String?,
    val labelQty: String?,
)

/**
 * Reads the barcodes scanned when adding items to an assembly.
 *
 * Supported formats:
 * - **This app's labels**: Code 128 containing the literal text
 *   `(01)<ITEMBARCODE>(10)<ORDER NUMBER>(37)<BOX QTY>`, with the 10 and 37
 *   parts optional. The batch is the assembly order number that made the
 *   product. Tin labels contain the plain item barcode only.
 * - **Supplier GS1 barcodes** (GS1-128, GS1 DataMatrix, GS1 QR) without
 *   brackets, e.g. `01093123450000171724123110ABC123`. Variable length fields
 *   are ended by the GS character when the scanner sends it.
 * - **Plain barcodes**, such as EAN-13, UPC or an item code.
 */
object BarcodeParser {

    private const val GROUP_SEPARATOR = '\u001D'

    /** GS1 application identifiers with a fixed data length. */
    private val fixedLengthAis = mapOf(
        "00" to 18, "01" to 14, "02" to 14,
        "11" to 6, "12" to 6, "13" to 6, "15" to 6, "16" to 6, "17" to 6,
        "20" to 2,
    )

    /** Common GS1 application identifiers with variable length data. */
    private val variableLengthAis = setOf(
        "10", "21", "22", "30", "37",
        "90", "91", "92", "93", "94", "95", "96", "97", "98", "99",
        "240", "241", "250", "251", "400", "401", "402", "403",
    )

    /** Matches `(AI)value` pairs, e.g. `(01)12345(10)A1234`. */
    private val bracketedAi = Regex("""\((\d{2,4})\)([^(]*)""")

    /** A symbology identifier some scanners put in front, e.g. `]C1`. */
    private val symbologyPrefix = Regex("""^\][A-Za-z][0-9A-Za-z]""")

    /**
     * Splits a scanned barcode into its item identifiers, batch and label qty.
     */
    fun parse(scanned: String): ScannedBarcode {
        val raw = scanned.trim().replace(symbologyPrefix, "")
        val ais = parseBracketed(raw) ?: parseUnbracketed(raw)

        if (!ais.isNullOrEmpty()) {
            val identifiers = listOfNotNull(ais["01"], ais["02"]) + raw
            return ScannedBarcode(
                raw = raw,
                identifiers = identifiers.distinct(),
                batch = ais["10"]?.takeIf { it.isNotBlank() },
                labelQty = (ais["37"] ?: ais["30"])?.takeIf { it.isNotBlank() },
            )
        }
        return ScannedBarcode(raw = raw, identifiers = listOf(raw), batch = null, labelQty = null)
    }

    /**
     * Reads the `(AI)value` text this app prints on its labels.
     *
     * @return The AI values, or null if the text is not in this format.
     */
    private fun parseBracketed(raw: String): Map<String, String>? {
        if (!raw.startsWith("(")) return null
        val matches = bracketedAi.findAll(raw).toList()
        if (matches.isEmpty() || matches.sumOf { it.value.length } != raw.length) return null
        return matches.associate { match ->
            match.groupValues[1] to match.groupValues[2].replace(GROUP_SEPARATOR.toString(), "").trim()
        }
    }

    /**
     * Reads a GS1 element string without brackets, as found on supplier
     * barcodes.
     *
     * Only text that clearly looks like GS1 is read: it either contains the GS
     * separator or starts with AI 01/02 and a full 14 digit GTIN. Reading stops
     * at the first AI that is not recognised.
     *
     * @return The AI values, or null if the text is not GS1.
     */
    private fun parseUnbracketed(raw: String): Map<String, String>? {
        val looksLikeGs1 = raw.contains(GROUP_SEPARATOR) || Regex("""^0[12]\d{14}""").containsMatchIn(raw)
        if (!looksLikeGs1) return null

        val result = linkedMapOf<String, String>()
        var i = 0
        while (i < raw.length) {
            if (raw[i] == GROUP_SEPARATOR) {
                i++
                continue
            }
            val ai = readAi(raw, i) ?: break
            i += ai.length

            val fixed = fixedLength(ai)
            if (fixed != null) {
                if (i + fixed > raw.length) break
                result[ai] = raw.substring(i, i + fixed)
                i += fixed
            } else {
                val end = raw.indexOf(GROUP_SEPARATOR, i).let { if (it == -1) raw.length else it }
                result[ai] = raw.substring(i, end)
                i = end
            }
        }
        return result.takeIf { it.isNotEmpty() }
    }

    /** Reads the application identifier starting at [start], if it is one this parser knows. */
    private fun readAi(raw: String, start: Int): String? {
        for (length in 2..4) {
            if (start + length > raw.length) return null
            val candidate = raw.substring(start, start + length)
            if (!candidate.all { it.isDigit() }) return null
            if (fixedLength(candidate) != null || candidate in variableLengthAis) return candidate
        }
        return null
    }

    /** The data length of a fixed length AI, or null if [ai] is variable length or unknown. */
    private fun fixedLength(ai: String): Int? {
        fixedLengthAis[ai]?.let { return it }
        // Trade measures, e.g. 3103 net weight (kg): 4 digit AI, 6 digits of data
        if (ai.length == 4 && ai.substring(0, 2).toInt() in 31..36) return 6
        // Global location numbers 410-417
        if (ai.length == 3 && ai.toInt() in 410..417) return 13
        return null
    }

    /**
     * Normalises a barcode or item code so differently formatted versions of
     * the same code compare equal. Numeric codes lose their leading zeros, so a
     * 14 digit GTIN matches the 13 digit EAN or 12 digit UPC it was made from.
     */
    fun normalise(code: String): String {
        val trimmed = code.trim().uppercase()
        return if (trimmed.isNotEmpty() && trimmed.all { it.isDigit() }) trimmed.trimStart('0') else trimmed
    }
}
