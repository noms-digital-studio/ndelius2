const TOGGLE_ATTRIBUTE_NAME = 'data-open'

/**
 * Track analytics event
 * @param event
 * @param category
 * @param label
 */
const trackEvent = (event, category, label) => {
  window.gtag && window.gtag('event', event, {'event_category': category, 'event_label': label})
}

/**
 * Check element for the attribute
 * @param $element
 * @return {boolean}
 */
const elementHasToggleAttribute = $element => {
  return $element.getAttribute(TOGGLE_ATTRIBUTE_NAME) === 'true'
}

/**
 * Add/remove the attribute
 * @param $element
 * @param isToggled
 */
const toggleElementFlag = ($element, isToggled) =>
  isToggled ? $element.removeAttribute(TOGGLE_ATTRIBUTE_NAME) : $element.setAttribute(TOGGLE_ATTRIBUTE_NAME, 'true')

/**
 * Check for attribute and toggle accordingly
 * @param $element
 * @return {boolean}
 */
const checkAndConfigureElementOpen = $element => {
  const hasAttribute = elementHasToggleAttribute($element)
  toggleElementFlag($element, hasAttribute)
  return hasAttribute
}

/**
 * Standard open/close element tracking config
 * @param $element
 * @param category
 * @param label
 */
const standardOpenCloseElementTracking = ($element, category, label) =>
  $element && $element.addEventListener('click', event => {
    const isOpen = checkAndConfigureElementOpen(event.target)
    trackEvent(isOpen ? 'close' : 'open', category, label)
  })

export {
  trackEvent,
  toggleElementFlag,
  elementHasToggleAttribute,
  checkAndConfigureElementOpen,
  standardOpenCloseElementTracking
}