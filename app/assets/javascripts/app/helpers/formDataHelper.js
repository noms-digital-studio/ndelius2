const updateFormData = ($formData) => {
  if (!$formData) {
    return
  }
  const RICH_TEXT = '<!-- RICH_TEXT -->'
  $formData.forEach(($item, $key) => {
    $item = $item.replace(`<p>${ RICH_TEXT }</p>`, '') // Correct an edge case where this is wrapped in a paragraph
    if (/<\s*[a-z]+[^>]*>(.*?)<\s*\/\s*[a-z]+>/ig.test($item) && $item.indexOf(RICH_TEXT) === -1) {
      $formData.set($key, RICH_TEXT + $item)
      const $formElement = document.getElementById($key)
      if ($formElement) {
        document.getElementById($key).value = RICH_TEXT + $item
      }
    }
  })
}

export {
  updateFormData
}
