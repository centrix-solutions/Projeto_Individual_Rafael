import Conexao.bancoSenha
import Conexao.bancoUser
import java.io.File

object ScriptPadraoPython {

    private var pythonProcesses: List<Process> = listOf()

    fun criarScript(idMaquinaDado: Int, idEmpresaDado: Int): Pair<String, String> {

        val codigoPythonDefaultHard = """
        import psutil
        import time
        import pymssql
        from mysql.connector import connect
        from datetime import datetime

        banco_user = '$bancoUser'
        banco_senha = '$bancoSenha'
        id_maquina_dado = '$idMaquinaDado'

        mysql_cnx = connect(user=banco_user, password=banco_senha, host='localhost', database='centrix')
        sql_server_cnx = pymssql.connect(server='44.197.21.59', database='centrix', user='sa', password='centrix')

        while True:
            try:
            
                data_e_hora_atuais = datetime.now()
                data_atual = data_e_hora_atuais.date()
                hora_atual = data_e_hora_atuais.time()

                CPU = round(psutil.cpu_percent(), 2)
                RAM = round(psutil.virtual_memory().used / (1024**3), 3)
                DISK = round(psutil.disk_usage('/').used / (1024**3), 3)

                bd_local_cursor = mysql_cnx.cursor()
  
                bd_local_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                        (data_atual, hora_atual, CPU, 1, 1, id_maquina_dado, id_maquina_dado))

                bd_local_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                        (data_atual, hora_atual, RAM, 3, 3, id_maquina_dado, id_maquina_dado))

                bd_local_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                        (data_atual, hora_atual, DISK, 2, 2, id_maquina_dado, id_maquina_dado))

                bd_local_cursor.close()
                mysql_cnx.commit()

                bd_server_cursor = sql_server_cnx.cursor()

                bd_server_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                        (str(data_atual), str(hora_atual), CPU, 1, 1, id_maquina_dado, id_maquina_dado))

                bd_server_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                        (str(data_atual), str(hora_atual), RAM, 2, 3, id_maquina_dado, id_maquina_dado))

                bd_server_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                        (str(data_atual), str(hora_atual), DISK, 3, 2, id_maquina_dado, id_maquina_dado))

                bd_server_cursor.close()
                sql_server_cnx.commit()

                time.sleep(20)

            except Exception as e:
                print(f"Ocorreu um erro: {e}")


    """.trimIndent()

        val codigoPythonDefaultRede = """
            import speedtest as st
            import time
            from mysql.connector import connect
            import pymssql
            from datetime import datetime

            banco_user = '$bancoUser'
            banco_senha = '$bancoSenha'
            id_maquina_dado = '$idMaquinaDado'
            id_empresa_dado = '$idEmpresaDado'

            
            cnx = connect(user=banco_user, password=banco_senha, host='localhost', database='centrix')
            sql_server_cnx = pymssql.connect(server='44.197.21.59', database='centrix', user='sa', password='centrix')

            speed_test = st.Speedtest()

            while True:
                try:
                    download = speed_test.download()
                    download_mbs = round(download / (10**6), 2)

                    upload = speed_test.upload()
                    upload_mbs = round(upload / (10**6), 2)

                    latencia = speed_test.results.ping

                    data_e_hora_atuais = datetime.now()
                    data_atual = data_e_hora_atuais.date()
                    hora_atual = data_e_hora_atuais.time()

                   
                    bd_cursor = cnx.cursor()

                    bd_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                     (data_atual, hora_atual, download_mbs, 5, 5, id_maquina_dado, id_empresa_dado))
                          
                    bd_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                     (data_atual, hora_atual, upload_mbs, 6, 6, id_maquina_dado, id_empresa_dado))

                    bd_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                     (data_atual, hora_atual, latencia, 9, 9, id_maquina_dado, id_empresa_dado))

                    cnx.commit()
                    bd_cursor.close()
        
                    bd_server_cursor = sql_server_cnx.cursor()

                    bd_server_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                            (str(data_atual), str(hora_atual), download_mbs, 5, 5, id_maquina_dado, id_empresa_dado))

                    bd_server_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                            (str(data_atual), str(hora_atual), upload_mbs, 6, 6, id_maquina_dado, id_empresa_dado))

                    bd_server_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                            (str(data_atual), str(hora_atual), latencia, 9, 9, id_maquina_dado, id_empresa_dado))

                    sql_server_cnx.commit()
                    bd_server_cursor.close()

                    time.sleep(20)

                except Exception as e:
                    print(f"Ocorreu um erro: {e}")
    """.trimIndent()


        val nomeArquivoPyDefaultHard = "centrixMonitoramentoHardware.py"
        File(nomeArquivoPyDefaultHard).writeText(codigoPythonDefaultHard)

        Thread.sleep(2 * 1000L)

        val nomeArquivoPyDefaultRede = "centrixMonitoramentoRede.py"
        File(nomeArquivoPyDefaultRede).writeText(codigoPythonDefaultRede)

        return Pair(nomeArquivoPyDefaultHard, nomeArquivoPyDefaultRede)

    }

    fun executarScript(arquivo1: String, arquivo2: String) {
        val pythonProcess1 = Runtime.getRuntime().exec("py $arquivo1")
        val pythonProcess2 = Runtime.getRuntime().exec("py $arquivo2")
        pythonProcesses = listOf(pythonProcess1, pythonProcess2)
    }

    fun pararScript() {
        for (process in pythonProcesses) {
            process.destroyForcibly()
        }
    }
}