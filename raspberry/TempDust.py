import struct
import serial
import Adafruit_DHT
import time
from Data import Database
from time import sleep
sensor = Adafruit_DHT.DHT11
pin = 3


db = Database()

global humi
global temper


# now time

humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)
if humidity is not None and temperature is not None:
    # while(1):
    now = time.localtime()
    nowtime = ("%04d/%02d/%02d %02d:%02d:%02d" % (now.tm_year,
               now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec))
#         print('Temp={0:0.1f}*C Humidity={1:0.1f}%'.format(temperature, humidity))
    humi = (round(humidity, 2))
    temper = (round(temperature, 2))
    # db.insert(nowtime,humi,temper)
    # time.sleep(30)
else:
    print('Failed to get reading. Try again!')


class PMS7003(object):

    # PMS7003 protocol data (HEADER 2byte + 30byte)
    PMS_7003_PROTOCOL_SIZE = 32

    # PMS7003 data list
    HEADER_HIGH = 0  # 0x42
    HEADER_LOW = 1  # 0x4d
    FRAME_LENGTH = 2  # 2x13+2(data+check bytes)
    DUST_PM1_0_CF1 = 3  # PM1.0 concentration unit μ g/m3（CF=1，standard particle）
    DUST_PM2_5_CF1 = 4  # PM2.5 concentration unit μ g/m3（CF=1，standard particle）
    DUST_PM10_0_CF1 = 5  # PM10 concentration unit μ g/m3（CF=1，standard particle）
    DUST_PM1_0_ATM = 6  # PM1.0 concentration unit μ g/m3（under atmospheric environment）
    DUST_PM2_5_ATM = 7  # PM2.5 concentration unit μ g/m3（under atmospheric environment）
    # PM10 concentration unit μ g/m3  (under atmospheric environment)
    DUST_PM10_0_ATM = 8
    # indicates the number of particles with diameter beyond 0.3 um in 0.1 L of air.
    DUST_AIR_0_3 = 9
    # indicates the number of particles with diameter beyond 0.5 um in 0.1 L of air.
    DUST_AIR_0_5 = 10
    # indicates the number of particles with diameter beyond 1.0 um in 0.1 L of air.
    DUST_AIR_1_0 = 11
    # indicates the number of particles with diameter beyond 2.5 um in 0.1 L of air.
    DUST_AIR_2_5 = 12
    # indicates the number of particles with diameter beyond 5.0 um in 0.1 L of air.
    DUST_AIR_5_0 = 13
    # indicates the number of particles with diameter beyond 10 um in 0.1 L of air.
    DUST_AIR_10_0 = 14
    RESERVEDF = 15  # Data13 Reserved high 8 bits
    RESERVEDB = 16  # Data13 Reserved low 8 bits
    CHECKSUM = 17  # Checksum code

    # header check

    def header_chk(self, buffer):

        if (buffer[self.HEADER_HIGH] == 66 and buffer[self.HEADER_LOW] == 77):
            return True

        else:
            return False

    # chksum value calculation
    def chksum_cal(self, buffer):

        buffer = buffer[0:self.PMS_7003_PROTOCOL_SIZE]

        # data unpack (Byte -> Tuple (30 x unsigned char <B> + unsigned short <H>))
        chksum_data = struct.unpack('!30BH', buffer)

        chksum = 0

        for i in range(30):
            chksum = chksum + chksum_data[i]

        return chksum

    # checksum check
    def chksum_chk(self, buffer):

        chk_result = self.chksum_cal(buffer)

        chksum_buffer = buffer[30:self.PMS_7003_PROTOCOL_SIZE]
        chksum = struct.unpack('!H', chksum_buffer)

        if (chk_result == chksum[0]):
            return True

        else:
            return False

    # protocol size(small) check
    def protocol_size_chk(self, buffer):

        if (self.PMS_7003_PROTOCOL_SIZE <= len(buffer)):
            return True

        else:
            return False

    # protocol check
    def protocol_chk(self, buffer):

        if (self.protocol_size_chk(buffer)):

            if (self.header_chk(buffer)):

                if (self.chksum_chk(buffer)):

                    return True
                else:
                    print("Chksum err")
            else:
                print("Header err")
        else:
            print("Protol err")

        return False

    # unpack data
    # <Tuple (13 x unsigned short <H> + 2 x unsigned char <B> + unsigned short <H>)>
    def unpack_data(self, buffer):

        buffer = buffer[0:self.PMS_7003_PROTOCOL_SIZE]

        # data unpack (Byte -> Tuple (13 x unsigned short <H> + 2 x unsigned char <B> + unsigned short <H>))
        data = struct.unpack('!2B13H2BH', buffer)

        return data

    def print_serial(self, buffer):

        chksum = self.chksum_cal(buffer)
        data = self.unpack_data(buffer)

        now = time.localtime()
        nowtime = ("%04d/%02d/%02d %02d:%02d:%02d" % (now.tm_year,
                   now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec))

        pm1 = data[self.DUST_PM1_0_CF1]
        pm15 = data[self.DUST_PM2_5_CF1]
        pm20 = data[self.DUST_PM10_0_CF1]

        global humi
        global temper
        db.insert(nowtime, pm1, pm15, pm20, humi, temper)

#         print(pm1, pm25, pm10)

#         print ("============================================================================")
#         print ("Header : %c %c \t\t | Frame length : %s" % (data[self.HEADER_HIGH], data[self.HEADER_LOW], data[self.FRAME_LENGTH]))
#         print ("PM 1.0 (CF=1) : %s\t | PM 1.0 : %s" % (data[self.DUST_PM1_0_CF1], data[self.DUST_PM1_0_ATM]))
#         print ("PM 2.5 (CF=1) : %s\t | PM 2.5 : %s" % (data[self.DUST_PM2_5_CF1], data[self.DUST_PM2_5_ATM]))
#         print ("PM 10.0 (CF=1) : %s\t | PM 10.0 : %s" % (data[self.DUST_PM10_0_CF1], data[self.DUST_PM10_0_ATM]))
#         print ("0.3um in 0.1L of air : %s" % (data[self.DUST_AIR_0_3]))
#         print ("0.5um in 0.1L of air : %s" % (data[self.DUST_AIR_0_5]))
#         print ("1.0um in 0.1L of air : %s" % (data[self.DUST_AIR_1_0]))
#         print ("2.5um in 0.1L of air : %s" % (data[self.DUST_AIR_2_5]))
#         print ("5.0um in 0.1L of air : %s" % (data[self.DUST_AIR_5_0]))
#         print ("10.0um in 0.1L of air : %s" % (data[self.DUST_AIR_10_0]))
#         print ("Reserved F : %s | Reserved B : %s" % (data[self.RESERVEDF],data[self.RESERVEDB]))
#         print ("CHKSUM : %s | read CHKSUM : %s | CHKSUM result : %s" % (chksum, data[self.CHECKSUM], chksum == data[self.CHECKSUM]))
#         print ("============================================================================")
        print(nowtime, pm1, pm15, pm20, humi, temper)


# UART / USB Serial : 'dmesg | grep ttyUSB'
USB0 = '/dev/ttyUSB0'
UART = '/dev/ttyAMA0'

# USE PORT
SERIAL_PORT = USB0

# Baud Rate
Speed = 9600


# example
if __name__ == '__main__':

    # serial setting
    #ser = serial.Serial(SERIAL_PORT, Speed, timeout = 1)
    ser = serial.Serial(SERIAL_PORT, Speed, timeout=2)

    dust = PMS7003()

    # while True:

    while True:
        ser.flushInput()
        buffer = ser.read(1024)

        if (dust.protocol_chk(buffer)):

            print("DATA read success")
            #db.insert(nowtime,pm1,pm25, pm10,humi,temper)
    #         print(nowtime, humi, temper)
    #         print(dust.pm1, dust.pm10, dust.pm15)
            # print data
            dust.print_serial(buffer)

        else:

            print("DATA read fail...")

        sleep(1800)

ser.close()
