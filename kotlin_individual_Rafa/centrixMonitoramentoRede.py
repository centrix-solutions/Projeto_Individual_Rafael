import speedtest as st
import time
from mysql.connector import connect
import pymssql
from datetime import datetime

banco_user = 'root'
banco_senha = '38762'
id_maquina_dado = '1'
id_empresa_dado = '1'


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