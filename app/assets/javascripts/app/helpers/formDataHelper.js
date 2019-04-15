/**
 * @FIXME: This could/should be part of the PDF Generator?
 */
const RICH_TEXT = '<!-- RICH_TEXT -->'

const updateFormData = ($formData) => {
  $formData.forEach(($item, $key) => {
    if ($item.indexOf('<p>') !== -1 && $item.indexOf(RICH_TEXT) === -1) {
      $formData.set($key, RICH_TEXT + $item)
    }
  })
}

export {
  RICH_TEXT,
  updateFormData
}
