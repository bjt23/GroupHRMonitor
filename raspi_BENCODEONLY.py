#!/usr/bin/env python
import time
import os
import RPi.GPIO as GPIO
import threading
import random
from bluetooth import *
import numpy as np
import datetime as dt

GPIO.setmode(GPIO.BCM)
DEBUG = 1
START = 1
RUN = 1
STOP = 0
YES = 1
NO = 0
SAMPLE_PERIOD = 5000 
CALCULATION_PERIOD = 3000000
FS = 1.0/SAMPLE_PERIOD

CALCULATION_CONSTANT = 2

FAILURE = 0
CONNECTION_ISSUE = 1
RESTING = 2
SLIGHTLY_ELEVATED = 3
ELEVATED = 4
VERY_ELEVATED = 5

#--------------------------------------BLUETOOTH SETUP-----------------------------
server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "SampleServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ], 
#                   protocols = [ OBEX_UUID ] 
                    )
print "Waiting for connection on RFCOMM channel %d" % port

#--------------------------------------BLUETOOTH THREAD--------------------------
def blueThread(trate, tstat):
	#try:
	client_sock, client_info = server_sock.accept()
	print str(trate)+','+str(tstat)
	client_sock.send(str(trate)+'\n')
	#except KeyboardInterrupt:
	#	print 'endthread'
	
print('threaded\n')


#--------------------------------------FUNCTION PROTOTYPE - READADC -------------------------------------
# section redacted, property of Matthew Rowe



#-------------------------------------FUNCTION PROTOTYPE DSP----------------------------
# section redacted, property of Matthew Rowe

#------------------------------------------INITIALISE------------------------
# section redacted, property of Matthew Rowe

#-------------------------------------START CODE!--------------------------------------

#INITIALISE VALUES
timeStart=dt.datetime.now()
executionTime = dt.datetime.now() - timeStart
lastInput = dt.datetime.now()
lastHeartCalc = dt.datetime.now()
firstTime = YES
MAX_LENGTH = round((CALCULATION_PERIOD/SAMPLE_PERIOD)*CALCULATION_CONSTANT)

previous_heart_rates = np.zeros(0)
data= np.zeros(0)
time_since = np.zeros(0)
threads=[]
counter = 0
quick_val = 0
previous_count = 0

#------------------------------WAIT FOR INITIAL CONNECTION----------------------------
client_sock, client_info = server_sock.accept()

print "Accepted connection from ", client_info
client_sock.send('0\n')
#calculation_iteration_val = 0

while (RUN):
	try:
		currentTime = dt.datetime.now()
		executionTime = currentTime-timeStart
		
		timeSinceMeasure = currentTime - lastInput
		timeSinceRateCalc = currentTime-lastHeartCalc
		
		#--------------------------------MEASURE VOLTAGE----------------------
		# section redacted, property of Matthew Rowe
		
		#---------------------------------HEART RATE AND SEND------------------
		# section redacted, property of Matthew Rowe
		
			#####	BEN		######
			sendToPhone=threading.Thread(target=blueThread, args=(heartRate, status,))
			sendToPhone.daemon=True
			threads.append(sendToPhone)
			sendToPhone.start()
			######################
			
			timeSinceRateCalc = 0
			lastHeartCalc = dt.datetime.now()
			
		#----------------------------------CHECK FOR STOP BUTTON--------------
		####	BEN		########
		
		#if (bluetooth(android) == STOP) 
		#	start = STOP
			#function-start #call the top function and jump to start  = this can be implemented later
	#GPIO.cleanup()
	#break
	
	except KeyboardInterrupt:
		print "\n\nKeyboard interupt..."
		GPIO.cleanup()
		
		break		
