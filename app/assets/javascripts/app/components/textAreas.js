import tinymce from 'tinymce/tinymce'
import 'tinymce/themes/silver/theme'
import 'tinymce/plugins/autoresize'
import 'tinymce/plugins/lists'

import { autoSaveProgress } from '../helpers/saveProgressHelper'
import { debounce } from '../utilities/debounce'

/**
 *
 * @param $editor
 */
function configureEditor ($editor) {
  const container = $editor.getContainer().querySelector('.tox-editor-container')
  const toolbar = container.querySelector('.tox-toolbar')
  toolbar.style.display = 'none'
  container.appendChild(toolbar)

  if ($editor.getElement().classList.contains('moj-textarea--prefilled')) {
    container.classList.add('tox-editor-container--prefilled')
  }
}

/**
 *
 * @param $editor
 */
function showToolbar ($editor) {
  const toolbar = $editor.getContainer().querySelector('.tox-toolbar')
  toolbar.style.display = 'flex'
}

/**
 *
 * @param $editor
 */
function hideToolbar ($editor) {
  const toolbar = $editor.getContainer().querySelector('.tox-toolbar')
  toolbar.style.display = 'none'
}

/**
 *
 * @param $editor
 */
function addPlaceholder ($editor) {
  if (!$editor.getContent({ format: 'text' }).trim().length) {
    $editor.getContainer().classList.add('tox-tinymce--placeholder')
  }
}

/**
 *
 * @param $editor
 */
function removePlaceholder ($editor) {
  $editor.getContainer().classList.remove('tox-tinymce--placeholder')
}

/**
 *
 * @param $editor
 */
function updateFormElement ($editor) {
  $editor.getElement().value = $editor.getContent()
}

/**
 *
 * @param $editor
 */
function updateTextLimits ($editor) {
  const messageHolder = document.getElementById(`${ $editor.id }-countHolder`)
  const messageTarget = document.getElementById(`${ $editor.id }-count`)

  if (!messageHolder || !messageTarget) {
    return
  }

  const limit = $editor.getElement().dataset.limit
  const current = $editor.getContent({ format: 'text' }).trim().length

  if (limit && current > 0) {
    messageHolder.classList.remove('govuk-visually-hidden')
    messageTarget.innerText = `${ limit } recommended characters, you have used ${ current }`
  } else {
    messageHolder.classList.add('govuk-visually-hidden')
  }
}

/**
 *
 * @param $editor
 */
function toggleFocusRectangle ($editor) {
  $editor.getContainer().querySelector('.tox-editor-container').classList.toggle('tox-tinymce--focus-rect')
}

/**
 *
 */
const initTextAreas = () => {
  tinymce.init({
    branding: false,
    menubar: false,
    browser_spellcheck: true,
    allow_conditional_comments: true,
    selector: '.govuk-textarea:not(.moj-textarea--classic)',
    plugins: ['autoresize lists'],
    toolbar: 'undo redo | bold italic underline | alignleft alignjustify | numlist bullist',
    width: '100%',
    min_height: 145,
    invalid_elements: 'span',
    valid_classes: {
      'p': ''
    },
    valid_styles: {
      'p': 'text-align'
    },
    skin_url: '/assets/skins/ui/oxide',
    setup: $editor => {
      $editor.on('init', () => {
        configureEditor($editor)
        addPlaceholder($editor)
        updateFormElement($editor)
      })
      $editor.on('focus', () => {
        toggleFocusRectangle($editor)
        showToolbar($editor)
        removePlaceholder($editor)
      })
      $editor.on('blur', () => {
        toggleFocusRectangle($editor)
        hideToolbar($editor)
        addPlaceholder($editor)
        updateFormElement($editor)
        autoSaveProgress($editor.getElement())
      })
      $editor.on('keyup', debounce(() => {
        updateFormElement($editor)
        autoSaveProgress($editor.getElement())
      }, 5000))
      $editor.on('input', () => {
        updateTextLimits($editor)
      })
    }
  })
}

export {
  initTextAreas
}
