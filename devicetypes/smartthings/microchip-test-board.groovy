/**
 *  Microchip Test Board
 *
 *  Author: SmartThings
 *  Date: 2014-02-20 10:15
 */

/*
SETUP INSTRUCTIONS
Attributes:
	-refresh
	-switch
(custom)
	-display
	-led1
	-led2
	-sw1
	-sw2
	-backlight
	-mfg
	-device
	-version
	-sub

Commands:
(custom)
	-discover
	-subscribe
	-setText
	-led1on
	-led1off
	-led2on
	-led2off
	-backlighton
	-backlightoff
*/
metadata 
{
	// Automatically generated. Make future change here.
	definition (name: "Microchip Test Board", namespace: "smartthings", author: "superuser") {
		capability "Refresh"
		capability "Switch"

		attribute "display", "string"
		attribute "led1", "string"
		attribute "led2", "string"
		attribute "sw1", "string"
		attribute "sw2", "string"
		attribute "backlight", "string"
		attribute "mfg", "string"
		attribute "device", "string"
		attribute "version", "string"
		attribute "sub", "string"

		command "discover"
		command "setText"
		command "led1on"
		command "led1off"
		command "led2on"
		command "led2off"
		command "backlighton"
		command "backlightoff"
		command "subscribe"
	}

	preferences 
	{
		input "displayText", "text", title: "Display Text (optional)", required: false
	}

	simulator 
	{	// define status and reply messages here
	}

	tiles 
	{	// define your main and details tiles here
		standardTile("icon", "icon", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
			state "default", label: "Microchip", action: "", icon: "st.unknown.thing.thing-circle", backgroundColor: "#FFFFFF"
		}
		valueTile("display", "device.display", decoration: "flat", height: 1, width: 3, inactiveLabel: false) {
			state "default", label:'${currentValue}'
		} 
		standardTile("led1", "device.led1", decoration: "flat", height: 1, width: 1, inactiveLabel: false) {
			state "off", label: 'led1', action: "led1on", icon: "st.switches.light.off", backgroundColor: "#ffffff"
			state "on", label: 'led1', action: "led1off", icon: "st.switches.light.on", backgroundColor: "#79b821"
		}
		standardTile("led2", "device.led2", decoration: "flat", height: 1, width: 1, inactiveLabel: false) {
			state "off", label: 'led2', action: "led2on", icon: "st.switches.light.off", backgroundColor: "#ffffff"
			state "on", label: 'led2', action: "led2off", icon: "st.switches.light.on", backgroundColor: "#79b821"
		}
		standardTile("backlight", "device.backlight", decoration: "flat", height: 1, width: 1, inactiveLabel: false) {
			state "off", label: 'backlight', action: "backlighton", icon: "st.switches.light.off", backgroundColor: "#ffffff"
			state "on", label: 'backlight', action: "backlightoff", icon: "st.switches.light.on", backgroundColor: "#79b821"
		}        
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		standardTile("setText", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Display Text", action:"setText", icon:""
		}
		standardTile("subscribe", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:"Subscribe", action:"subscribe", icon:""
		}	
		standardTile("sw1", "device.sw1", width:1, height: 1, canChangeIcon: true) {
			state "off", label: 'SW1 ${name}', action: "", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: 'SW1 ${name}', action: "", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}
		standardTile("sw2", "device.sw2", width:1, height: 1, canChangeIcon: true) {
			state "off", label: 'SW2 ${name}', action: "", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: 'SW2 ${name}', action: "", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}        
		standardTile("led1sw", "device.led1", width:1, height: 1, canChangeIcon: true) {
			state "off", label: '${name}', action: "led1.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "led1.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}
		valueTile("version", "device.version", decoration: "flat", height: 1, width: 3,inactiveLabel: false) {
			state "version", label:'version: ${currentValue}', unit:"" 
		}
		valueTile("sub", "device.sub", decoration: "flat",height: 1, width: 3, inactiveLabel: false) {
			state "sub", label:'subpath: ${currentValue}', unit:"" 
		}
		valueTile("mfg", "device.mfg", decoration: "flat",height: 1, width: 3, inactiveLabel: false) {
			state "mfg", label:'manufacturer: ${currentValue}', unit:"" 
		}
		valueTile("device", "device.device", decoration: "flat", height: 1, width: 3, inactiveLabel: false) {
			state "device", label:'device: ${currentValue}', unit:"" 
		}

		main (["icon"])
		details(["display", "led1", "led2", "backlight",  "setText", "sw1", "sw2", "refresh", "subscribe", "mfg", "device", "version", "sub"])
	}
}


def parse(String description) 
{	// parse events into attributes
	log.debug "Parsing '${description}'"
	def result = []
	def parsedEvent = stringToMap(description)

	if (parsedEvent.headers && parsedEvent.body) 
	{
		def headerString = new String(parsedEvent.headers.decodeBase64())
		def bodyString = new String(parsedEvent.body.decodeBase64())
		log.debug "parse() - ${bodyString}"

		def body = new groovy.json.JsonSlurper().parseText(bodyString)
		if (body) 
		{
			if (body.resource)
			{
				if(body.resource == "/")
				{
					log.debug "body(/) - ${body}"
					result << createEvent(name: "display", value: body.display)
					result << createEvent(name: "led1", value: (body.led1 == 1) ? "on" : "off")
					result << createEvent(name: "led2", value: (body.led2 == 1) ? "on" : "off")
					result << createEvent(name: "backlight", value: (body.backlight == 1) ? "on" : "off")
					result << createEvent(name: "sw1", value: (body.sw1 == 1) ? "on" : "off")
					result << createEvent(name: "sw2", value: (body.sw2 == 1) ? "on" : "off")
					result << createEvent(name: "mfg", value: body.mfg)
					result << createEvent(name: "device", value: body.device)
					result << createEvent(name: "version", value: body.version)
					result << createEvent(name: "sub", value: body.sub)	
				}
				else if(body.resource == "/sub")
				{
					log.debug "body(/sub) - ${body}"
					result << createEvent(name: "subID", value: body.id)
					result << createEvent(name: "subIP", value: body.ip)
					result << createEvent(name: "subPort", value: body.port)
				}
			}
		}
	}
	result
}

def led1on() 
{
	sendHTTPPOST("", [led1:1])
}

def led1off() 
{
	sendHTTPPOST("", [led1:0])
}

def led2on() 
{
	sendHTTPPOST("", [led2:1])
}

def led2off() 
{
	sendHTTPPOST("", [led2:0])
}

def backlighton() 
{
	sendHTTPPOST("", [backlight:1])
}

def backlightoff() 
{
	sendHTTPPOST("", [backlight:0])
}

def discover() 
{
	log.debug "Executing 'discover'"
	// TODO: handle 'discover' command
	//new physicalgraph.device.HubAction("lan discover mdns/dns-sd ._services._dns-sd._udp.local", physicalgraph.device.Protocol.LAN)
	sendHTTPPOST("", [led1:1])
}

def subscribe() 
{
	log.debug "Executing 'subscribe'"
	sendHTTPPOST("sub", [id:1234, ip:getHubAddressIP(), port:getHubAddressPort()])
}

def setText() 
{
	log.debug "Executing 'setText'"
	def text = displayText ?: "Herro"
	sendHTTPPOST("", [display:text])
}

def refresh() 
{
	log.debug "Executing 'refresh'"
	sendHTTPGET("")
}

def on() 
{
	log.debug "Executing 'on'"    
	sendHTTPPOST("", [led1:1, led2:1, backlight:1])
}

def off() 
{
	log.debug "Executing 'off'"
	sendHTTPPOST("", [led1:0, led2:0, backlight:0])
}

private Integer convertHexToInt(hex) 
{
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) 
{
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() 
{
	def parts = device.deviceNetworkId.split(":")
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}

private getHubAddressIP()
{
    def ip = device.hub.getDataValue("localIP")
	return ip
}

private getHubAddressPort()
{
    def port = device.hub.getDataValue("localSrvPortTCP").toInteger() 
	return port
}

private sendHTTPGET(path) 
{
	def uri = "/$path"
	new physicalgraph.device.HubAction("""GET $uri HTTP/1.1\r\nHOST: ${getHostAddress()}\r\n\r\n""", 
		physicalgraph.device.Protocol.LAN)
}

private sendHTTPPOST(path, body) 
{
	def uri = "/$path"
	def bodyJSON = new groovy.json.JsonBuilder(body).toString()
	def length = bodyJSON.getBytes().size().toString()

	log.debug "POST:  $uri"
	log.debug "BODY: ${bodyJSON}"

	new physicalgraph.device.HubAction("""POST $uri HTTP/1.1\r\nHOST: ${getHostAddress()}\r\nContent-Length: ${length}\r\n\r\n${bodyJSON}""", 
		physicalgraph.device.Protocol.LAN)
}
