const featureSwitch = {
    isEnabled: (cookies, feature) => {
        const cookieState = feature => cookies.get(toSwitchValue(feature)) === 'true'

        switch (switchState(feature)) {
            case 'must':
                return true
            case 'allowed':
                return cookieState(feature)
            default:
                return false
        }
    }
}

const switchState = feature => window[toSwitchValue(feature)]
const toSwitchValue = feature => `feature${camelCase(feature)}`
const camelCase = word => word.charAt(0).toUpperCase() + word.slice(1)

export default featureSwitch;