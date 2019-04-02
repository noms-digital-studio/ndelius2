(function ($) {

  'use strict'

  $(function () {

    /**
     * Start save icon
     * @param elem
     */
    function startSaveIcon (elem) {
      var saveIcon = $('#save_indicator'),
        spinner = $('.spinner, .moj-auto-save__spinner', saveIcon)

      saveIcon.removeClass('govuk-visually-hidden')
      spinner.removeClass('error')
      spinner.addClass('active')
    }

    /**
     * End save icon
     * @param elem
     * @param error
     */
    function endSaveIcon (elem, error) {
      var saveIcon = $('#save_indicator'),
        spinner = $('.spinner, .moj-auto-save__spinner', saveIcon),
        errorMessage = $('#' + elem.attr('id') + '-autosave_error'),
        formGroup = $(elem).closest('.form-group')

      if (error) {
        saveIcon.addClass('govuk-visually-hidden')
        spinner.removeClass('active')
        errorMessage.removeClass('govuk-visually-hidden')
        formGroup.addClass('form-group-autosave-error')
      } else {
        // remove all autosave errors on this page
        $('.form-group-autosave-error').removeClass('form-group-autosave-error')
        $('.autosave-error-message').addClass('govuk-visually-hidden')
        spinner.removeClass('active')
      }
    }

    /**
     * Save
     */
    function saveProgress (elem) {
      var $form = document.getElementById('ndForm')
      var formData = new FormData($form)

      if ($form) {
        startSaveIcon(elem)
        formData.append('jumpNumber', '0')

        var xhr = new XMLHttpRequest()
        xhr.open('POST', `${ $form.getAttribute('action') }/save`)
        xhr.onload = function() {
          _.delay(endSaveIcon, 500, elem, xhr.status !== 200)
        }
        xhr.send(formData)
      }
    }

    var quietSaveProgress = _.debounce(saveProgress, 5000)

    function saveAndUpdateTextLimits (editor, lengthCalculator) {
      quietSaveProgress(editor)

      var limit = editor.data('limit'),
        current = lengthCalculator(editor),
        messageHolder = $('#' + editor.attr('id') + '-countHolder'),
        messageTarget = $('#' + editor.attr('id') + '-count')

      if (limit && current > 0) {
        messageHolder.removeClass('govuk-visually-hidden')
        messageTarget.text(limit + ' recommended characters, you have used ' + current)
      } else {
        messageHolder.addClass('govuk-visually-hidden')
      }
    }

    $('textarea').keyup(function () {
      var editor = $(this)
      saveAndUpdateTextLimits(editor, function () {
        return editor.val().length
      })
    })

    $('input[type="text"], input[type="number"]').blur(function () {
      var editor = $(this)
      quietSaveProgress(editor)
    })

    $('input[type="radio"], input[type="checkbox"]').click(function () {
      var editor = $(this)
      quietSaveProgress(editor)
    })

    $('textarea:not(.classic)').each(function (i, elem) {
      convertToEditor($(elem))
    })

    // toggle editors back to visible previous hidden while quill initialises
    $('textarea').each(function (i, elem) {
      $(elem).css('visibility', 'visible')
    })

    function replaceTextArea (textArea) {
      var attributesNotToBeCopied = ['name', 'placeholder', 'role']
      var areaAttributes = attributesNotToBeCopied.reduce(function (accumulator, currentValue) {
        accumulator[currentValue] = textArea.attr(currentValue)
        return accumulator
      }, {})
      var value = textArea.val()
      var editor = $('<div>' + value + '</div>')
      $.each(textArea[0].attributes, function (index, element) {
        if (attributesNotToBeCopied.indexOf(this.name) === -1) {
          editor.attr(this.name, this.value)
        }
      })

      textArea.replaceWith(editor)
      editor.after('<input type="hidden" name="' + areaAttributes.name + '" value=""/>')
      editor.addClass('text-area-editor')
      return areaAttributes
    }

    function convertToEditor (textArea) {
      var id = '#' + textArea.attr('id')
      var areaAttributes = replaceTextArea(textArea)

      var editor = new Quill(id, {
        placeholder: areaAttributes.placeholder,
        theme: 'snow',
        formats: ['bold', 'italic', 'underline', 'list', 'align'],
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline'],
            [{ align: '' }, { align: 'justify' }],
            [{ 'list': 'ordered' }, { 'list': 'bullet' }],
            ['clean']
          ]
        }
      })

      $(id).find('.ql-editor').attr('role', areaAttributes.role)

      function cleanHtml () {
        return '<!-- RICH_TEXT -->' + editor.root.innerHTML.replace(/<br>/gm, '<br/>')
      }

      function hasAnyText () {
        return editor.getText().trim()
      }

      function transferValueToInput () {
        if (hasAnyText()) {
          $('input[name=\'' + areaAttributes.name + '\']').val(cleanHtml())
        } else {
          $('input[name=\'' + areaAttributes.name + '\']').val('')
        }
      }

      function withModifier (text) {
        if (/Mac/i.test(navigator.platform)) {
          return text
        }
        return text.replace('⌘', 'Ctrl-').replace('⇧', 'Shift-')
      }

      editor.on('text-change', function (delta, oldDelta, source) {
        transferValueToInput()
      })

      transferValueToInput()

      // swap toolbar to below editor
      var toolbar = $(id).prev()
      toolbar.before($(id))

      // Include class to hide toolbar by default
      $(id).next().addClass('govuk-visually-hidden')

      function addTooltipsToToolbar () {
        toolbar.find('button').addClass('tooltip').addClass('moj-tooltip')
        var tips = [
          { selector: '.ql-bold', tooltip: 'Bold (⌘B)' },
          { selector: '.ql-italic', tooltip: 'Italic (⌘I)' },
          { selector: '.ql-underline', tooltip: 'Underline (⌘U)' },
          { selector: '.ql-align[value="justify"]', tooltip: 'Justify (⌘⇧J)' },
          { selector: '.ql-align[value=""]', tooltip: 'Align Left (⌘⇧L)' },
          { selector: '.ql-list[value="ordered"]', tooltip: 'Numbered List' },
          { selector: '.ql-list[value="bullet"]', tooltip: 'Bulleted List' },
          { selector: '.ql-clean', tooltip: 'Remove Formatting' }]

        function addTooltop (tip) {
          toolbar.find(tip.selector + ' svg').after(withModifier('<span>' + tip.tooltip + '</span>'))
        }

        tips.forEach(addTooltop)
      }

      // show/hide toolbar with focus change
      editor.on('selection-change', function (range) {
        if (range) {
          $(id).next().removeClass('govuk-visually-hidden')
        } else {
          $(id).next().addClass('govuk-visually-hidden')
        }
      })

      editor.on('text-change', function () {
        saveAndUpdateTextLimits($(id), function () {
          return editor.getText().trim().length
        })
      })

      // add classes to reduce margins
      $(id).closest('.form-group').addClass('small-margin-bottom')
      $(id).closest('.form-group').addClass('govuk-!-margin-bottom-0')
      $(id).closest('.form-group').next('hr').addClass('small-margin-top')
      $(id).closest('.form-group').next('hr').addClass('govuk-!-margin-top-0')

      // remove tab key binding for editor, toolbar (and for IE11 svg)
      delete editor.getModule('keyboard').bindings[9]
      editor.getModule('keyboard').addBinding({
        key: 'J',
        shiftKey: true,
        shortKey: true
      }, function (range, context) {
        this.quill.format('align', 'justify')
      })
      editor.getModule('keyboard').addBinding({
        key: 'L',
        shiftKey: true,
        shortKey: true
      }, function (range, context) {
        this.quill.format('align', '')
      })
      toolbar.find(':button').attr('tabindex', '-1')
      toolbar.find('svg').attr('focusable', 'false')

      addTooltipsToToolbar()
    }

    //===========================================
    // ANALYTICS
    //===========================================

    var isSfr = window.location.pathname.indexOf('shortFormatPreSentenceReport') !== -1
    var isParom = window.location.pathname.indexOf('paroleParom1Report') !== -1

    // Analytics for 'What to include' links on SFR and PAROM1
    $('summary').click(function () {

      if (isSfr || isParom) {
        var details = $(this).parent(),
          isOpen = details.attr('data-open') === 'true',
          label = details.parent().find('.govuk-caption-xl').text() ||
            details.parent().parent().find('legend').find('span').text() ||
            details.parent().parent().find('legend').text() ||
            details.prev('span:not(.govuk-hint)').text() ||
            details.prev('span').prev('span').text() || 'Unknown field'

        isOpen ? details.removeAttr('data-open') : details.attr('data-open', 'true')

        gtag('event', isOpen ? 'close' : 'open', {
          'event_category': (isSfr ? 'SFR' : 'PAROM1') + ' - What to include',
          'event_label': $('h1').text() + ' > ' + label.trim()
        })
      }
    })
  })

})(window.jQuery)