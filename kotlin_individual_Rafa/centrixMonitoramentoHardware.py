import psutil
import time
import pymssql
from mysql.connector import connect
from datetime import datetime

banco_user = 'root'
banco_senha = '38762'
id_maquina_dado = '1'

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
                                (data_atual, hora_atual, RAM, 2, 3, id_maquina_dado, id_maquina_dado))

        bd_local_cursor.execute("INSERT INTO Monitoramento (Data_captura, Hora_captura, Dado_Capturado, fkCompMonitorados, fkCompMoniExistentes, fkMaqCompMoni, fkEmpMaqCompMoni) VALUES (%s, %s, %s, %s, %s, %s, %s)",
                                (data_atual, hora_atual, DISK, 3, 2, id_maquina_dado, id_maquina_dado))

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

