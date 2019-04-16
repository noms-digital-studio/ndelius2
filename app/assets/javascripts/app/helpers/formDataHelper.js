const updateFormData = ($formData) => {
  if (!$formData) {
    return
  }
  const RICH_TEXT = '<!-- RICH_TEXT -->'
  $formData.forEach(($item, $key) => {
    if ($item.indexOf('<p>') !== -1 && $item.indexOf(RICH_TEXT) === -1) {
      $formData.set($key, RICH_TEXT + $item)
      document.getElementById($key).value = RICH_TEXT + $item
    }
  })
}

export {
  updateFormData
}
