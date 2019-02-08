/*

 Offender summary > Accordion analytics

 Because the GOV.UK Accordion listens for click events and events don't fire in any particular order we have to
 configure our own logic on the click event and the open/closed state.

 Element open/closed state can be updated by web storage state and/or by Open/Close all functionality.

*/

import { nodeListForEach } from 'govuk-frontend/common'
import {
  checkAndConfigureElementOpen,
  elementHasToggleAttribute,
  toggleElementFlag,
  trackEvent
} from './analyticsHelper'

/**
 * Get NodeList of accordion button elements
 * @return {NodeListOf<Element>}
 */
const allAccordionButtons = () => {
  return document.querySelectorAll('.govuk-accordion__section-button')
}

/**
 * Get Open/Close all button element
 * @return {Element}
 */
const openAllButton = () => {
  return document.querySelector('.govuk-accordion__open-all')
}

/**
 * Walk through the individual accordion elements and toggle attribute on Open/Close all button element accordingly
 */
const updateOpenCloseAllAccordionsElementBasedOnAccordionState = () => {
  let allOpen = true
  nodeListForEach(allAccordionButtons(), $accordion => {
    if (!elementHasToggleAttribute($accordion)) {
      allOpen = false
    }
  })
  toggleElementFlag(openAllButton(), !allOpen)
}

/**
 * Configure the accordion attributes based on initial state - we can use the aria-expanded attribute set by GOV.UK Accordion reliably here
 */
const configureInitialAccordionState = () => {
  const checkAndConfigureIfElementExpanded = $element => {
    const isExpanded = $element.getAttribute('aria-expanded') === 'true'
    toggleElementFlag($element, !isExpanded)
  }
  checkAndConfigureIfElementExpanded(openAllButton())
  nodeListForEach(allAccordionButtons(), $accordion => checkAndConfigureIfElementExpanded($accordion))
}

/**
 * Configure Open/Close all button actions
 */
const configureOpenCloseAllAccordionsElement = () =>
  openAllButton().addEventListener('click', event => {
    const isOpen = checkAndConfigureElementOpen(event.target)
    trackEvent(isOpen ? 'close-all' : 'open-all', 'Offender summary > Accordion', 'Open/Close all')
    nodeListForEach(allAccordionButtons(), $accordion => toggleElementFlag($accordion, isOpen))
  })

/**
 * Configure individual accordion buttons actions
 */
const configureIndividualAccordionElements = () =>
  nodeListForEach(allAccordionButtons(), $accordion => {
    $accordion.addEventListener('click', event => {
      const $button = event.target
      trackEvent(checkAndConfigureElementOpen($button) ? 'close' : 'open', 'Offender summary > Accordion', $button.textContent)
      updateOpenCloseAllAccordionsElementBasedOnAccordionState()
    })
  })

/**
 * Main offender summary page accordion config
 */
const configureOffenderSummaryAccordionTracking = () => {
  configureInitialAccordionState()
  configureOpenCloseAllAccordionsElement()
  configureIndividualAccordionElements()
}

export {
  configureOffenderSummaryAccordionTracking
}