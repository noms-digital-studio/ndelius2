import { nodeListForEach } from '../utilities/nodeListForEach'

/**
 * Covers left-hand report section navigation and check your report pages
 */
const initNavigation = () => {
  nodeListForEach(document.querySelectorAll('.js-nav-item'), $navItem => {
    $navItem.addEventListener('click', event => {
      event.preventDefault()
      const target = event.target.dataset.target
      if (target && !event.target.parentNode.classList.contains('moj-subnav__section-item--current')) {
        document.getElementById('jumpNumber').value = target
        document.getElementById('ndForm').submit()
      }
    })
  })
}

export {
  initNavigation
}
