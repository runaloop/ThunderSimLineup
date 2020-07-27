package com.catp.thundersimlineup.data.db.operation


import com.catp.model.JsonLineup
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.LineupEntity
import com.catp.thundersimlineup.data.db.entity.TeamEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UpdateLineupsTeamsTest {
    @MockK(relaxed = true)
    lateinit var dao: LineupDao

    @MockK(relaxed = true)
    lateinit var changeset: Changeset

    @InjectMockKs
    lateinit var updateLineups: UpdateTeams

    private lateinit var jsonLineups: List<JsonLineup>
    private lateinit var newTeams: List<TeamEntity>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        jsonLineups = mutableListOf(JsonLineup("1_1"), JsonLineup("1_2"))
        newTeams = listOf(
            TeamEntity("1_1", "A"),
            TeamEntity("1_1", "B"),
            TeamEntity("1_2", "A"),
            TeamEntity("1_2", "B")
        )
    }

    @Test
    fun `no local teams at all, create pairs from jsonLineup`() {
        //GIVEN
        every { dao.getLineupsEntity() } answers { emptyList() }
        daoGetTeamTableAfterInsert()
        //WHEN
        updateLineups.process(jsonLineups)

        //THEN
        verify { dao.insertTeams(eq(newTeams)) }
        verify {
            dao.insertLineups(
                eq(
                    listOf(
                        LineupEntity(jsonLineups[0].name, 1, 2),
                        LineupEntity(jsonLineups[1].name, 3, 4)
                    )
                )
            )
        }
    }

    @Test
    fun `have local teams, but jsonLineup have 1 new, insert teams and lineup`() {
        //GIVEN
        every { dao.getLineupsEntity() } answers { listOf(LineupEntity(jsonLineups[0].name, 1, 2)) }
        daoGetTeamTableAfterInsert()

        //WHEN
        updateLineups.process(jsonLineups)

        //THEN
        verify { dao.insertTeams(eq(newTeams.filter { it.lineupName != jsonLineups[0].name })) }

        verify {
            dao.insertLineups(
                eq(
                    listOf(
                        LineupEntity(jsonLineups[1].name, 3, 4)
                    )
                )
            )
        }
    }

    @Test
    fun `have local teams, and same lineup count in jsonLineup do nothing`() {
        //GIVEN
        every { dao.getLineupsEntity() } answers {
            listOf(
                LineupEntity(jsonLineups[0].name, 1, 2),
                LineupEntity(jsonLineups[1].name, 3, 4)
            )
        }

        //WHEN
        updateLineups.process(jsonLineups)

        //THEN
        verify(exactly = 0) { dao.insertTeams(any()) }
        verify(exactly = 0) { dao.insertLineups(any()) }
    }

    private fun daoGetTeamTableAfterInsert() {
        every { dao.getTeamTable() } answers {
            newTeams.mapIndexed { index, team ->
                TeamEntity(team.lineupName, team.teamLetter, index + 1L)
            }
        }
    }

}