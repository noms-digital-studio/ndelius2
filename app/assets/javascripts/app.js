import { initAll } from 'govuk-frontend'
import { initAppAll } from './app/initAll'

document.addEventListener('DOMContentLoaded', () => {
  initAll()
  initAppAll()
})
