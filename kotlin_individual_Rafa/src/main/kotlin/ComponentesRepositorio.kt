
import org.springframework.jdbc.core.JdbcTemplate

class ComponentesRepositorio {
    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var jdbcTemplateServer: JdbcTemplate

    fun iniciar() {

        jdbcTemplate = Conexao.jdbcTemplate!!
        jdbcTemplateServer = Conexao.jdbcTemplateServer!!
    }


    fun buscarIdMaqPorId(idProcessador: String): Int {
        return jdbcTemplateServer.queryForObject(
            "SELECT idMaquina FROM Maquinas WHERE Id_do_dispositivo = ?",
            arrayOf(idProcessador),
            Int::class.java
        )
    }

    fun buscarComponetesMaq(idMaquina: Int): List<Int> {
        return jdbcTemplateServer.queryForList(
            "SELECT fkComponentesExistentes FROM maquinas AS m JOIN componentes_monitorados AS c ON m.idMaquina = c.FKMaquina WHERE idMaquina = ?;",
            arrayOf(idMaquina),
            Int::class.java
        )
    }

    fun buscarIdComp(fkEmpresa: Int, fkMaquina: Int, fkComponentesExistentes: Int): Int {
        return jdbcTemplateServer.queryForObject(
            "SELECT idComponente_monitorado FROM componentes_monitorados WHERE fkEmpMaqComp = ? AND fkMaquina = ? AND fkComponentesExistentes = ?;",
            arrayOf(fkEmpresa, fkMaquina, fkComponentesExistentes),
            Int::class.java
        )
    }

    fun registrarComponente(valor: Double, fkComponente: Int, idMaq: Int, novaMaquina: Maquina) {
        jdbcTemplateServer.update(
            """
        INSERT INTO Componentes_Monitorados (valor, fkComponentesExistentes, fkMaquina, fkEmpMaqComp)
        VALUES (?, ?, ?, ?)
        """.trimIndent(), valor, fkComponente, idMaq, novaMaquina.fkEmpMaq
        )

    }
}
