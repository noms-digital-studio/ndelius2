(function ($) {

  'use strict'

  $(function () {

    /**
     * Start save icon
     * @param elem
     */
    function startSaveIcon (elem) {
      var spinner = document.querySelector('.moj-auto-save__spinner')

      document.getElementById('save_indicator').classList.remove('govuk-visually-hidden')
      spinner.classList.remove('error')
      spinner.classList.add('active')
    }

    /**
     * End save icon
     * @param elem
     * @param error
     */
    function endSaveIcon (elem, error) {
      document.querySelector('.moj-auto-save__spinner').classList.remove('active')

      if (error) {
        document.getElementById('save_indicator').classList.add('govuk-visually-hidden')
        elem.closest('.form-group').classList.add('form-group-autosave-error')
        document.getElementById(elem.id + '-autosave_error').classList.remove('govuk-visually-hidden')
      } else {
        // remove all autosave errors on this page
        var autosaveError = document.querySelector('.form-group-autosave-error')
        var autosaveErrorMessage = document.querySelector('.autosave-error-message')
        if (autosaveError && autosaveErrorMessage) {
          autosaveError.classList.remove('form-group-autosave-error')
          autosaveErrorMessage.classList.add('govuk-visually-hidden')
        }
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
        xhr.open('POST', $form.getAttribute('action') + '/save')
        xhr.onload = function () {
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

      var toolbarId = 'toolbar-'+textArea.attr('id')
      var undoId = 'undo-'+textArea.attr('id')
      var undoSvg = "<svg version=\"1.1\" id=\"Capa_1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 485.183 485.183\" style=\"enable-background:new 0 0 485.183 485.183;\" xml:space=\"preserve\"><g><path d=\"M257.751,113.708c-74.419,0-140.281,35.892-181.773,91.155L0,128.868v242.606h242.606l-82.538-82.532 c21.931-66.52,84.474-114.584,158.326-114.584c92.161,0,166.788,74.689,166.788,166.795 C485.183,215.524,383.365,113.708,257.751,113.708z\"/></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g></svg>"
      var toolbarElement = "<div class='ql-toolbar' id='"+toolbarId+"'><span class='ql-formats'><button class='ql-undo' id='"+undoId+"'>"+undoSvg+"</button></span><span class='ql-formats'><button class='ql-bold'></button><button class='ql-italic'></button><button class='ql-underline'></button></span><span class='ql-formats'><button class='ql-align' value=''></button><button class='ql-align' value='justify'></button></span><span class='ql-formats'><button class='ql-list' value='ordered'></button><button class='ql-list' value='bullet'></button></span><span class='ql-formats'><button class='ql-clean'></button></span></div>"
      $('.govuk-grid-row').append(toolbarElement)

      var editor = new Quill(id, {
        placeholder: areaAttributes.placeholder,
        theme: 'snow',
        formats: ['bold', 'italic', 'underline', 'list', 'align'],
        modules: {
          history: {
            delay: 2000,
            maxStack: 500,
            userOnly: true
          },
          toolbar: '#'+toolbarId
        }
      })

      var undoButton = document.querySelector('#'+undoId)
      undoButton.addEventListener('click', function() {
        editor.history.undo()
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
      var toolbar = $("#"+toolbarId)
      $(id).after(toolbar);

      // Include class to hide toolbar by default
      $(".ql-toolbar").addClass('govuk-visually-hidden')

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
          { selector: '.ql-clean', tooltip: 'Remove Formatting' },
          { selector: '.ql-undo', tooltip: 'Undo (⌘Z)' }]

        function addTooltop (tip) {
          toolbar.find(tip.selector + ' svg').after(withModifier('<span>' + tip.tooltip + '</span>'))
        }

        tips.forEach(addTooltop)
      }

      // show/hide toolbar with focus change
      editor.on('selection-change', function (range) {
        if (range) {
          $("#"+toolbarId).removeClass('govuk-visually-hidden')
        } else {
          $("#"+toolbarId).addClass('govuk-visually-hidden')
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
  })

})(window.jQuery)