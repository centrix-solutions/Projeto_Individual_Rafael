
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

    fun registrarComponente(valor: Double, fkComponente: Int, idMaq: Int, novaMaquina: Maquina) {
        jdbcTemplateServer.update(
            """
        INSERT INTO Componentes_Monitorados (valor, fkComponentesExistentes, fkMaquina, fkEmpMaqComp)
        VALUES (?, ?, ?, ?)
        """.trimIndent(), valor, fkComponente, idMaq, novaMaquina.fkEmpMaq
        )

    }
}
