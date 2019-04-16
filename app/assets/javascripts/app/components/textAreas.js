import tinymce from 'tinymce/tinymce'
import 'tinymce/themes/silver/theme'
import 'tinymce/plugins/autoresize'
import 'tinymce/plugins/lists'

import { autoSaveProgress } from '../helpers/saveProgressHelper'
import { debounce } from '../utilities/debounce'

function updateTextLimits ($editor) {
  const messageHolder = document.getElementById(`${ $editor.id }-countHolder`)
  const messageTarget = document.getElementById(`${ $editor.id }-count`)

  if (!messageHolder || !messageTarget) {
    return
  }

  const limit = document.getElementById($editor.id).dataset.limit
  const current = $editor.getContent().replace(/(<([^>]+)>)/ig, '').replace('&nbsp;', '').trim().length

  if (limit && current > 0) {
    messageHolder.classList.remove('govuk-visually-hidden')
    messageTarget.innerText = `${ limit } recommended characters, you have used ${ current }`
  } else {
    messageHolder.classList.add('govuk-visually-hidden')
  }
}

function updateFormElement ($editor) {
  document.getElementById($editor.id).value = $editor.getContent()
}

const initTextAreas = () => {
  tinymce.init({
    branding: false,
    menubar: false,
    browser_spellcheck: true,
    allow_conditional_comments: true,
    selector: '.govuk-textarea ',
    plugins: ['autoresize lists'],
    toolbar: 'undo redo | bold italic underline | alignleft alignjustify | numlist bullist',
    setup: $editor => {
      $editor.on('init', () => {
        const container = document.getElementById($editor.id).parentNode.querySelector('.tox-editor-container')
        const toolbar = container.querySelector('.tox-toolbar')
        toolbar.style.display = 'none'
        container.appendChild(toolbar)
      })
      $editor.on('focus', () => {
        const container = document.getElementById($editor.id).parentNode.querySelector('.tox-editor-container')
        const toolbar = container.querySelector('.tox-toolbar')
        toolbar.style.display = 'flex'
      })
      $editor.on('blur', () => {
        updateFormElement($editor)
        autoSaveProgress(document.getElementById($editor.id))
        const toolbar = document.getElementById($editor.id).parentNode.querySelector('.tox-toolbar')
        toolbar.style.display = 'none'
      })
      $editor.on('keyup', debounce(() => {
        updateFormElement($editor)
        autoSaveProgress(document.getElementById($editor.id))
      }))
      $editor.on('input', () => {
        updateTextLimits($editor)
      })
    }
  })
}

export {
  initTextAreas
}
