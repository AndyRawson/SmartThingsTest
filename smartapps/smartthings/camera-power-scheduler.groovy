/**
 *  Schedule the Camera Power
 *
 *  Author: danny@smartthings.com
 *  Date: 2013-10-07
 */

definition(
    name: "Camera Power Scheduler",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Turn the power on and off at a specific time. ",
    category: "Available Beta Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/dropcam-on-off-schedule.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/dropcam-on-off-schedule@2x.png"
)

preferences {
	section("Camera power..."){
		input "switch1", "capability.switch", multiple: true
	}
	section("Turn the Camera On at..."){
		input "startTime", "time", title: "Start Time", required:false
	}
	section("Turn the Camera Off at..."){
		input "endTime", "time", title: "End Time", required:false
	}    
}

def installed()
{
	initialize()
}

def updated()
{
	unschedule()
	initialize()
}

def initialize() {
    /*
	def tz = location.timeZone
    
    //if it's after the startTime but before the end time, turn it on
    if(startTime && timeToday(startTime,tz).time > timeToday(now,tz).time){
    
      if(endTime && timeToday(endTime,tz).time < timeToday(now,tz).time){
        switch1.on()
      }
      else{
        switch1.off()
      }
    }
    else if(endTime && timeToday(endtime,tz).time > timeToday(now,tz).time)
    {
      switch1.off()
    }
    */
    
    if(startTime)
      runDaily(startTime, turnOnCamera)
    if(endTime)
      runDaily(endTime,turnOffCamera)
}

def turnOnCamera()
{
  log.info "turned on camera"
  switch1.on()
}

def turnOffCamera()
{
  log.info "turned off camera"
  switch1.off()
}
