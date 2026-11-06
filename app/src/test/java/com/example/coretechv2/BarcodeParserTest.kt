package com.example.coretechv2

import com.example.coretechv2.repository.BarcodeParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BarcodeParserTest {

    @Test
    fun appProductLabel_readsBarcodeAndOrderNumberAsBatch() {
        val scan = BarcodeParser.parse("(01)9312345000017(10)A10234")
        assertEquals("9312345000017", scan.identifiers.first())
        assertEquals("A10234", scan.batch)
        assertNull(scan.labelQty)
    }

    @Test
    fun appBoxLabel_readsBoxQty() {
        val scan = BarcodeParser.parse("(01)9312345000017(10)A10234(37)12")
        assertEquals("A10234", scan.batch)
        assertEquals("12", scan.labelQty)
    }

    @Test
    fun appBoxLabel_withoutOrderNumber_hasNoBatch() {
        val scan = BarcodeParser.parse("(01)9312345000017(37)6")
        assertNull(scan.batch)
        assertEquals("6", scan.labelQty)
    }

    @Test
    fun tinLabel_plainBarcode() {
        val scan = BarcodeParser.parse("9312345000017")
        assertEquals(listOf("9312345000017"), scan.identifiers)
        assertNull(scan.batch)
    }

    @Test
    fun supplierGs1_withSeparator() {
        // (01) GTIN, (17) expiry, (10) batch, GS, (21) serial
        val scan = BarcodeParser.parse("0109312345000017172512311" + "0LOT42\u001D21SER1")
        assertEquals("09312345000017", scan.identifiers.first())
        assertEquals("LOT42", scan.batch)
    }

    @Test
    fun supplierGs1_batchLast_noSeparator() {
        val scan = BarcodeParser.parse("010931234500001710B-7781")
        assertEquals("09312345000017", scan.identifiers.first())
        assertEquals("B-7781", scan.batch)
    }

    @Test
    fun supplierGs1_withSymbologyPrefixAndWeight() {
        // ]C1 prefix, (01) GTIN, (3103) net weight 2.500 kg, (10) batch
        val scan = BarcodeParser.parse("]C10109312345000017310300250010XY9")
        assertEquals("09312345000017", scan.identifiers.first())
        assertEquals("XY9", scan.batch)
    }

    @Test
    fun plainItemCode_isNotTreatedAsGs1() {
        val scan = BarcodeParser.parse("RESIN-450")
        assertEquals(listOf("RESIN-450"), scan.identifiers)
        assertNull(scan.batch)
    }

    @Test
    fun normalise_matchesGtin14ToEan13() {
        assertEquals(BarcodeParser.normalise("9312345000017"), BarcodeParser.normalise("09312345000017"))
        assertEquals("RESIN-450", BarcodeParser.normalise(" resin-450 "))
    }
}
