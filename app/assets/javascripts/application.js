function formWithZeroJumpNumber(form) {
    return _.map(form.serializeArray(), function (entry) {
        if (entry.name === 'jumpNumber') {
            entry.value = 0;
        }
        return entry;
    });
}

function openPopup(url) {
    window.open(url, 'reportpopup', 'top=200,height=760,width=820,resizable=yes,scrollbars=yes,location=no,menubar=no,status=yes,toolbar=no').focus();
}


(function ($) {

    'use strict';

    $(function () {

        // Show/hide content
        var showHideContent = new GOVUK.ShowHideContent();

        showHideContent.init();

        // Stick at top when scrolling
        GOVUK.stickAtTopWhenScrolling.init();

        /**
         *
         * @param parent
         * @param child
         */
        function showHint(parent, child) {
            child.hasClass('js-hidden') ? child.removeClass('js-hidden') : child.addClass('js-hidden');

            if (child.hasClass('js-hidden')) {
                parent.removeClass('active');
                parent.attr('aria-expanded', 'false');
                child.attr('aria-hidden', 'true');
            } else {
                parent.addClass('active');
                parent.attr('aria-expanded', 'true');
                child.attr('aria-hidden', 'false');
            }
        }

        /**
         * Start save icon
         * @param elem
         */
        function startSaveIcon(elem) {
            var saveIcon = $('#save_indicator'),
                spinner = $('.spinner', saveIcon);

            saveIcon.removeClass('js-hidden');
            spinner.removeClass('error');
            spinner.addClass('active');
        }

        /**
         * End save icon
         * @param elem
         * @param error
         */
        function endSaveIcon(elem, error) {
            var saveIcon = $('#save_indicator'),
                spinner = $('.spinner', saveIcon),
                errorMessage = $('#' + elem.attr('id') + '-autosave_error'),
                formGroup = $(elem).closest('.form-group');

            if (error) {
                saveIcon.addClass('js-hidden');
                spinner.removeClass('active');
                errorMessage.removeClass('js-hidden');
                formGroup.addClass('form-group-autosave-error');
            } else {
                // remove all autosave errors on this page
                $('.form-group-autosave-error').removeClass('form-group-autosave-error');
                $('.autosave-error-message').addClass('js-hidden');
                spinner.removeClass('active');
            }
        }

        /**
         * Save
         */
        function saveProgress(elem) {
            if ($('form').length) {
                startSaveIcon(elem);
                $.ajax({
                    type: 'POST',
                    url: $('form').attr('action') + '/save',
                    data: formWithZeroJumpNumber($('form')),
                    complete: function(response) {
                        _.delay(endSaveIcon, 500, elem, response.status !== 200)
                    }
                });
            }
        }

        var quietSaveProgress = _.debounce(saveProgress, 500);

        function saveAndUpdateTextLimits(editor, lengthCalculator) {
            quietSaveProgress(editor);

            var limit = editor.data('limit'),
                current = lengthCalculator(editor),
                messageHolder = $('#' + editor.attr('id') + '-countHolder'),
                messageTarget = $('#' + editor.attr('id') + '-count');

            if (limit && current > 0) {
                messageHolder.removeClass('js-hidden');
                messageTarget.text(limit + ' recommended characters, you have used ' + current);
            } else {
                messageHolder.addClass('js-hidden');
            }

        }


         $('textarea').keyup(function () {
             var editor = $(this)
             saveAndUpdateTextLimits(editor, function() {
                 return editor.val().length
             })
        })

        /**
         * Navigation items
         */
        $('.nav-item').click(function (e) {

            e.preventDefault();

            var target = $(this).data('target');
            if (target && !$(this).hasClass('active')) {
                $('#jumpNumber').val(target);
                $('form').submit();
            }
        });

        /**
         * Feedback link - change form action and submit
         */
        $('.feedback-link').click(function (e) {
            e.preventDefault();

            var form = $('form');
            form.attr('action', form.attr('action') + '/feedback').submit();
        });

        /**
         * Ensure jumpNumber is cleared if next after clicking browser back button
         */
        $('#nextButton:not(.popup-launcher)').click(function () {
            $('#jumpNumber').val('');
        });

        /**
         * Save and exit
         */
        $('#exitLink').click(function (e) {
            e.preventDefault();
            $('#jumpNumber').val(0);
            $('form').submit();
        });

        /**
         *
         */
        $('a.expand-content').each(function (i, elem) {
            var parent = $(this),
                child = $('#' + elem.getAttribute('data-target'));
            parent.attr('aria-controls', elem.getAttribute('data-target'));
            parent.click(function () {
                showHint(parent, child)
            });
        });

        // Progressive enhancement for browsers > IE8
        if (!$('html').is('.lte-ie8')) {
            // Autosize all Textarea elements (does not support IE8).
            autosize(document.querySelectorAll('textarea.classic'));

            // Autocomplete
            var autoComplete = document.querySelector('.auto-complete');
            if (autoComplete) {
                accessibleAutocomplete.enhanceSelectElement({
                    selectElement: autoComplete,
                    name: autoComplete.id,
                    defaultValue: '',
                    required: true
                });
            }

            // Date picker
            $('.date-picker').datepicker({
                dateFormat: 'dd/mm/yy'
            }).parent().addClass('date-wrapper');

            // htmlunit no longer supports IE8 or conditionals so also check agent to rule out IE8
            // not needing once we upgrade away from HTMLUnit
            if (navigator.userAgent.indexOf('MSIE 8.0') === -1) {
                $('textarea:not(.classic)').each(function (i, elem) {
                    convertToEditor($(elem))
                })
            }
            // toggle editors back to visible previous hidden while quill initialises
            $('textarea').each(function (i, elem) {
                $(elem).css('visibility', 'visible')
            })

        }

        function replaceTextArea(textArea) {
            var attributesNotToBeCopied = ['name', 'placeholder', 'role']
            var areaAttributes = attributesNotToBeCopied.reduce(function(accumulator, currentValue) {
                accumulator[currentValue] = textArea.attr(currentValue)
                return accumulator
            }, {})
            var value = textArea.val()
            var editor = $('<div>'+value+'</div>')
            $.each(textArea[0].attributes, function(index, element) {
                if (attributesNotToBeCopied.indexOf(this.name) === -1) {
                    editor.attr(this.name, this.value)
                }
            });

            textArea.replaceWith(editor)
            editor.after('<input type="hidden" name="'+areaAttributes.name+'" value=""/>')
            editor.addClass('text-area-editor')
            return areaAttributes

        }

        function convertToEditor(textArea) {
            var id = '#' + textArea.attr('id')
            var areaAttributes = replaceTextArea(textArea)

            var editor = new Quill(id, {
                placeholder: areaAttributes.placeholder,
                theme: 'snow',
                formats: ['bold', 'italic', 'underline', 'list'],
                modules: {
                    toolbar: [
                        ['bold', 'italic', 'underline'],
                        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                        ['clean']
                    ]
                }
            })

            $(id).find('.ql-editor').attr('role', areaAttributes.role)

            function cleanHtml() {
                return '<!-- RICH_TEXT -->' +  editor.root.innerHTML.replace(/<br>/gm,'<br/>')
            }

            function hasAnyText() {
                return editor.getText().trim()
            }

            function transferValueToInput() {
                if (hasAnyText()) {
                    $("input[name='"+ areaAttributes.name + "']").val(cleanHtml())
                } else {
                    $("input[name='"+ areaAttributes.name + "']").val('')
                }
            }

            function ensureCaretInView() {
                var footer = $('footer')
                var footerPosition = footer.offset()
                var position = $(id).find('.ql-editor').caret('offset')

                // ensure caret position doesn't go below footer
                if (position.top + (position.height * 2) > footerPosition.top) {
                    $('html, body').animate({
                        scrollTop: position.top - footer.height()
                    }, 100)
                }
            }

            function withModifier(text) {
                if (/Mac/i.test(navigator.platform)) {
                    return text
                }
                return text.replace('⌘', 'Ctrl-')
            }



            editor.on('text-change', function(delta, oldDelta, source) {
                if (source === 'user') {
                    transferValueToInput()
                }
            })

            transferValueToInput()

            // swap toolbar to below editor
            var toolbar = $(id).prev()
            toolbar.before($(id));

            function addTooltipsToToolbar() {
                toolbar.find('button').addClass('tooltip')
                var tips = [
                    {selector: '.ql-bold', tooltip: 'Bold (⌘B)'},
                    {selector: '.ql-italic', tooltip: 'Italic (⌘I)'},
                    {selector: '.ql-underline', tooltip: 'Underline (⌘U)'},
                    {selector: '.ql-list[value="ordered"]', tooltip: 'Numbered List'},
                    {selector: '.ql-list[value="bullet"]', tooltip: 'Bulleted List'},
                    {selector: '.ql-clean', tooltip: 'Remove Formatting'}]

                function addTooltop(tip) {
                    toolbar.find(tip.selector + ' svg').after(withModifier('<span>'+tip.tooltip+'</span>'))
                }
                tips.forEach(addTooltop)
            }

            // show/hide toolbar with focus change
            editor.on('selection-change', function(range) {
                if (range) {
                    $(id).next().css('visibility', 'visible')
                    ensureCaretInView()
                } else {
                    $(id).next().css('visibility', 'hidden')
                }
            })

            editor.on('text-change', function() {
                saveAndUpdateTextLimits($(id), function() {
                    return editor.getText().trim().length
                })
                ensureCaretInView()
            });

            // add classes to reduce margins
            $(id).closest('.form-group').addClass('small-margin-bottom')
            $(id).closest('.form-group').next('hr').addClass('small-margin-top')

            // remove tab key binding for editor, toolbar (and for IE11 svg)
            delete editor.getModule('keyboard').bindings[9];
            toolbar.find(':button').attr('tabindex', '-1')
            toolbar.find('svg').attr('focusable', 'false')

            addTooltipsToToolbar()

        }


        var elementSelector = '.ql-editor,input[type!=hidden],textarea'
        $('form:first').find(elementSelector).first().focus();

        function ensureFieldInView() {
            var element = $(this)
            var footer = $('footer')
            var footerPosition = footer.offset()
            var position = element.offset()

            // ensure no focused element falls below footer
            if (position.top + element.height() > footerPosition.top) {
                $('html, body').animate({
                    scrollTop: element.offset().top - footer.height()
                }, 100)
            }
        }


        $(elementSelector).focus(ensureFieldInView)

    });

    /**
     * Reveal or hide the other role section when 'Other' is chosen in the role drop down
     */
    $(document).on('change','#role',function(e){
        if ($('#role option:selected').text() === 'Other') {
            $('#roleother-section').removeClass('js-hidden')
        } else {
            $('#roleother-section').addClass('js-hidden');
        }
    });
})(window.jQuery);
