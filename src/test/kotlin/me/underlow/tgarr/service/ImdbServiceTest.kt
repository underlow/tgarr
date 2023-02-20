package me.underlow.tgarr.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ImdbServiceTest{
    @Test
    fun `test imdb url parsing`(){
        val url = "https://www.imdb.com/title/tt0089755/?ref_=wl_li_tt"
        val service = ImdbService()

        assertEquals("tt0089755", service.getImdbIdOrNull(url))
    }
}
