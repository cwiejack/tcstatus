package de.swp.services

import com.nhaarman.mockito_kotlin.*
import de.swp.MonitorIdBuilder
import de.swp.model.MonitorId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class MonitorServiceTest {

    lateinit var service: MonitorService


    val monitorDataService = mock<MonitorDataService>()

    @BeforeEach
    internal fun setUp() {
        service = MonitorService(arrayListOf(monitorDataService))
    }

    @Test
    fun retrieve() {
        val monitorId1 = MonitorIdBuilder(id = "testId").build()
        val monitorId2 = MonitorIdBuilder(id = "testId").build()
        val monitorId3 = MonitorIdBuilder(id ="another").build()

        whenever(monitorDataService.isResponsibleFor(monitorId1)).thenReturn(true)

        service.retrieve(arrayListOf(monitorId1,monitorId2,monitorId3))

        argumentCaptor<List<MonitorId>>().apply {
            verify(monitorDataService, atLeastOnce()).retrieve(capture())

            assertThat(firstValue).containsOnly(monitorId1, monitorId2)
        }
    }

}