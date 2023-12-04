import com.github.britooo.looca.api.core.Looca
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class Monitoramento {

    private var idEmpresa: Int = 0
    private val scanner = Scanner(System.`in`)
    private val looca = Looca()
    private val usuarioLogado = Usuario()
    private val repositorioUser = UsuarioRepositorio()
    private val repositorioMaquina = MaquinaRepositorio()
    private val repositorioComponentes = ComponentesRepositorio()
    private val repositorioMonitoramento = MonitoramentoRepositorio()



    companion object {
        private val cor_roxa = "\u001B[38;2;180;0;255m"

        val BANNER_LOGIN =
            " $cor_roxa ██████╗███████╗███╗   ██╗████████╗██████╗ ██╗██╗  ██╗                   \n" +
                "██╔════╝██╔════╝████╗  ██║╚══██╔══╝██╔══██╗██║╚██╗██╔╝                   \n" +
                "██║     █████╗  ██╔██╗ ██║   ██║   ██████╔╝██║ ╚███╔╝                    \n" +
                "██║     ██╔══╝  ██║╚██╗██║   ██║   ██╔══██╗██║ ██╔██╗                    \n" +
                "╚██████╗███████╗██║ ╚████║   ██║   ██║  ██║██║██╔╝ ██╗                   \n" +
                " ╚═════╝╚══════╝╚═╝  ╚═══╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝                   \n" +
                "                                                                         \n" +
                "███████╗ ██████╗ ██╗     ██╗   ██╗████████╗██╗ ██████╗ ███╗   ██╗███████╗\n" +
                "██╔════╝██╔═══██╗██║     ██║   ██║╚══██╔══╝██║██╔═══██╗████╗  ██║██╔════╝\n" +
                "███████╗██║   ██║██║     ██║   ██║   ██║   ██║██║   ██║██╔██╗ ██║███████╗\n" +
                "╚════██║██║   ██║██║     ██║   ██║   ██║   ██║██║   ██║██║╚██╗██║╚════██║\n" +
                "███████║╚██████╔╝███████╗╚██████╔╝   ██║   ██║╚██████╔╝██║ ╚████║███████║\n" +
                "╚══════╝ ╚═════╝ ╚══════╝ ╚═════╝    ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝"
    }

    fun inicioMoni() {
        repositorioUser.iniciar()
        repositorioMaquina.iniciar()
        repositorioComponentes.iniciar()
        repositorioMonitoramento.iniciar()
        while (true) {
            exibirBannerLogin()
            realizarLogin()

        }
    }

    private fun exibirBannerLogin() {
        println(BANNER_LOGIN)
    }

    private fun realizarLogin() {
        while (true) {
            println("-----login-----")
            println("")
            Thread.sleep(1000L)
            println("Digite o seu email:")
            val logarUsuarioEmail = scanner.nextLine()
            println("")
            println("Digite sua senha:")
            Thread.sleep(2000L)
            val logarUsuarioSenha = scanner.nextLine()
            println("")

            val autenticado = repositorioUser.autenticarLogin(logarUsuarioEmail, logarUsuarioSenha)

            if (autenticado) {
                println("Login bem-sucedido!")
                val user = repositorioUser.logarFuncionario(logarUsuarioEmail, logarUsuarioSenha)

                usuarioLogado.idFuncionario = user!!.idFuncionario
                usuarioLogado.nome = user.nome
                usuarioLogado.email = user.email
                usuarioLogado.senha = user.senha
                usuarioLogado.fkEmpFunc = user.fkEmpFunc
                usuarioLogado.fkNivelAcesso = user.fkNivelAcesso

                idEmpresa = user.fkEmpFunc
                println("Bem vindo ${usuarioLogado.nome}")
                break
            } else {
                println("Email ou senha incorretos. Tente novamente.")
            }
        }
        verificarEIniciarMonitoramento()
    }

    private fun verificarEIniciarMonitoramento() {
        val id = looca.processador.id
        val verificacao = repositorioMaquina.autenticarMaquina(id)

        if (!verificacao) {
            println("")
            println("Essa máquina não existe na base de dados")
            Thread.sleep(1000L)
            println("Iniciando o cadastro.....")

            val novaMaquina = Maquina()

            novaMaquina.SO = looca.sistema.sistemaOperacional
            novaMaquina.idCPU = looca.processador.id
            novaMaquina.fkEmpMaq = usuarioLogado.fkEmpFunc

            repositorioMaquina.registrarMaquina(novaMaquina, usuarioLogado)
            val idMaquina: Int = repositorioComponentes.buscarIdMaqPorId(id)

            val valores = listOf(
                100.0, //cpu 1
                looca.grupoDeDiscos.tamanhoTotal.toDouble() / 1000000000, //disco 2
                looca.memoria.total.toDouble() / 1000000000,//ram 3
                looca.dispositivosUsbGrupo.totalDispositvosUsbConectados.toDouble(), //usb 4
                0.0, // taxa_dowload 5
                0.0, // taxa_upload 6
                0.0, // janelas do sistema 7
                0.0, // processos 8
                0.0, // latencia 9
            )
            val componentes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

            for (i in valores.indices) {
                val valor = valores[i]
                val fkComponente = componentes[i]
                repositorioComponentes.registrarComponente(valor, fkComponente, idMaquina, novaMaquina)
            }

            println("Máquina cadastrada com monitoramento padrão.....")
            Thread.sleep(2000L)
        } else {
            println("Essa máquina já foi cadastrada")
        }

        val idMaquina: Int = repositorioComponentes.buscarIdMaqPorId(id)
        val maquinaSpecs = Maquina()

        maquinaSpecs.SO = looca.sistema.sistemaOperacional
        maquinaSpecs.DISCO = looca.grupoDeDiscos.tamanhoTotal.toDouble() / 1000000000
        maquinaSpecs.idCPU = looca.processador.id
        maquinaSpecs.RAM = looca.memoria.total.toDouble() / 1000000000
        maquinaSpecs.CPU = looca.processador.nome

        val ram = maquinaSpecs.RAM
        val disco = maquinaSpecs.DISCO
        val tempoAtt = looca.sistema.tempoDeAtividade
        val dias = tempoAtt / 86400
        val horas = (tempoAtt % 86400) / 3600
        val minutos = ((tempoAtt % 86400) % 3600) / 60

       val tempoAttFormat = String.format("%d dias, %02d:%02d horas", dias, horas, minutos)

        println("")
        println(
            """
        Especificações do seu computador:
        ID: ${maquinaSpecs.idCPU}.
        SO: ${maquinaSpecs.SO}.
        CPU: ${maquinaSpecs.CPU}.
        RAM: %.2f GB.
        DISCO: %.2f GB.
        Tempo de atividade: $tempoAttFormat.
        """.trimIndent().format(ram, disco)
        )
        println("")

        val horaLogin = LocalDateTime.now()

        repositorioUser.registrarLogin(usuarioLogado, idMaquina, maquinaSpecs, horaLogin)


        val componentesExistentes: List<String> =
            listOf("Cpu", "Disco", "Ram", "Usb", "Taxa Download", "Taxa Upload", "Janelas do Sistema", "Processos", "Latência")


        iniciarMonitoramento(idMaquina, idEmpresa, componentesExistentes)
    }

    private fun iniciarMonitoramento(
        idMaquina: Int,
        idEmpresa: Int,

        componentesExistentes: List<String>
    ) {
        val (arquivo1, arquivo2) = ScriptPadraoPython.criarScript(idMaquina, idEmpresa)
        println("Iniciando o monitoramento....")
        var opcaoMonitoramento = true

        ScriptPadraoPython.executarScript(arquivo1, arquivo2)
        val monitoramentoThread = thread {
            while (opcaoMonitoramento) {
                val atividade = looca.grupoDeJanelas.janelas[3].titulo
                val atividadeCaracter = atividade.replace(Regex("[^a-zA-Z0-9 ]"), "")
                val atividadeFormatada = atividadeCaracter.take(30)

                repositorioUser.atualizarAtividade(usuarioLogado, idMaquina, atividadeFormatada )

                val dados: MutableList<Float> = mutableListOf()

                val fkcomponentesExistentes: MutableList<Int> = mutableListOf()

                if (componentesExistentes.contains("Usb")) {
                    val usb: Float = looca.dispositivosUsbGrupo.totalDispositvosUsbConectados.toFloat()
                    dados.add(usb)
                    fkcomponentesExistentes.add(4)
                }
                if (componentesExistentes.contains("Janelas do Sistema")) {
                    val janelas: Float = looca.grupoDeJanelas.totalJanelas.toFloat()
                    dados.add(janelas)
                    fkcomponentesExistentes.add(7)
                }
                if (componentesExistentes.contains("Processos")) {
                    val processos: Float = looca.grupoDeProcessos.totalProcessos.toFloat()
                    dados.add(processos)
                    fkcomponentesExistentes.add(8)
                }
                for (i in dados.indices) {
                    val zonaFusoHorario = ZoneId.of("America/Sao_Paulo")
                    val data = LocalDate.now()
                    val hora = LocalTime.now(zonaFusoHorario)
                    val dado = dados[i]
                    val fkcompExis = fkcomponentesExistentes[i]
                    repositorioMonitoramento.registrarDados(
                        data,
                        hora,
                        dado,
                        fkcompExis,
                        idMaquina,
                        idEmpresa
                    )
                }
                Thread.sleep(20 * 1000L)
            }
        }
        val menuThread = thread {
            var opcaoMenu = true

            while (opcaoMenu) {
                println(
                    """
                Digite....
                1-Trocar de usuário
                2-Encerrar o programa
            """.trimIndent()
                )
                when (scanner.nextInt()) {
                    1 -> {
                        opcaoMenu = false
                        opcaoMonitoramento = false
                        ScriptPadraoPython.pararScript()

                        val horaLogout = LocalDateTime.now()
                        repositorioUser.registrarSaida(usuarioLogado, idMaquina, horaLogout)
                    }

                    2 -> {
                        println("Encerrando o programa...")

                        ScriptPadraoPython.pararScript()
                        opcaoMonitoramento = false

                        val horaLogout = LocalDateTime.now()

                        repositorioUser.registrarSaida(usuarioLogado, idMaquina, horaLogout)

                        exitProcess(0)
                    }

                    else -> {
                        println("Opção inválida. Por favor, escolha uma opção válida.")
                    }
                }
            }
        }
        monitoramentoThread.join()
        menuThread.join()
    }
}
