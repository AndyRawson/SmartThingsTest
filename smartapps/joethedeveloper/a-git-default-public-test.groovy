/**
 *  Big Turn OFF
 *
 *  Author: SmartThings
 */
definition(
    name: "A Git Default Public Test",
    namespace: "joethedeveloper",
    author: "SmartThings",
    description: "Turn your lights off when the SmartApp is tapped or activated",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {
	section("When I touch the app, turn off...") {
		input "switches", "capability.switch", multiple: true
	}
}

def installed()
{
	subscribe(location, changedLocationMode)
	subscribe(app, appTouch)
}

def updated()
{
	unsubscribe()
	subscribe(location, changedLocationMode)
	subscribe(app, appTouch)
}

def changedLocationMode(evt) {
	log.debug "changedLocationMode: $evt"
	switches?.off()
}

def appTouch(evt) {
	log.debug "appTouch: $evt"
	switches?.off()
}
