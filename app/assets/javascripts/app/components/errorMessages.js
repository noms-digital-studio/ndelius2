import { nodeListForEach } from '../utilities/nodeListForEach'

const reorderErrorMessages = () => {

  const $container = document.querySelector('.govuk-error-summary__list:not(.app-fixed-order)')
  if (!$container) {
    return;
  }
  $container.innerHTML = ''

  nodeListForEach(document.querySelectorAll('.govuk-error-message:not(.govuk-visually-hidden)'), $errorMessage => {
    const markup = `<li><a href="#${ $errorMessage.parentNode.querySelector('[id*="-error"]').id }" class="error-message">${ $errorMessage.textContent }</a></li>`
    const content = new DOMParser().parseFromString(markup, 'text/html')
    $container.appendChild(content.querySelector('li'))
  })
}

export {
  reorderErrorMessages
}
