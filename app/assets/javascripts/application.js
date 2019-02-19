function formWithZeroJumpNumber(form) {
    return _.map(form.serializeArray(), function (entry) {
        if (entry.name === 'jumpNumber') {
            entry.value = 0;
        }
        return entry;
    });
}

function openPopup(url, name, top, left) {
    window.open(url, (name || 'reportpopup'), 'top=' + (top || '200') + ',left=' + (left || '0') + ',width=1024,height=768,resizable=yes,scrollbars=yes,location=no,menubar=no,status=yes,toolbar=no').focus();
}

(function ($) {

    'use strict';

    $(function () {

        var isSfr = window.location.pathname.indexOf('shortFormatPreSentenceReport') !== -1;
        var isParom = window.location.pathname.indexOf('paroleParom1Report') !== -1;

        if (window.hasOwnProperty('GOVUK')) {
            // Show/hide content
            var showHideContent = new GOVUK.ShowHideContent();

            showHideContent.init();

            // Stick at top when scrolling
            GOVUK.stickAtTopWhenScrolling.init();
        }

        // Init GOVUKFrontend
        if (window.hasOwnProperty('GOVUKFrontend')) {
            window.GOVUKFrontend.initAll();

            var nodeListForEach = function(nodes, callback) {
                if (window.NodeList.prototype.forEach) {
                    return nodes.forEach(callback)
                }
                for (var i = 0; i < nodes.length; i++) {
                    callback.call(window, nodes[i], i, nodes)
                }
            };

            // @TODO: If this is to be a pattern, refine and include in the MOJ Pattern Library
            nodeListForEach(document.querySelectorAll('[data-module="progressive-radios"]'), function ($module) {

                var $inputs = $module.querySelectorAll('input[type="radio"]');
                nodeListForEach($inputs, function ($input) {
                    var controls = $input.getAttribute('data-aria-controls');
                    controls = controls ? controls.substr(0, controls.lastIndexOf('-')) + '-progressive' : '';

                    // Check if input controls anything and content exists.
                    if (!controls || !$module.querySelector('#' + controls)) {
                        return;
                    }

                    $input.setAttribute('data-aria-controls', controls);
                    $input.removeAttribute('_');
                    $input.addEventListener('click', function() {
                        activate(controls);
                    });

                    if ($input.checked) {
                        activate(controls);
                    }
                });

                function activate($id) {
                    $module.querySelector('#' + $id).classList.toggle('govuk-radios__conditional--hidden', false);

                    nodeListForEach($inputs, function ($input) {
                        $input.setAttribute('aria-expanded', $input.checked);
                    });
                }
            });
        }

        // Feedback links
        $('#feedbackForm,#footerFeedbackForm').click(function (e) {
            e.preventDefault();
            openPopup($(this).attr('href'), 'feedbackForm', 250, 50);
        });

        // Empty the error message list and repopulate with correct ordered list
        $('.govuk-error-summary__list').empty();
        $('.govuk-error-message:not(.js-hidden)').each(function (index, element) {
            $('.govuk-error-summary__list').append('<li><a href="#' + $('[id*="-error"]', $(element).parent()).attr('id') + '" class="error-message">' + $(element).text() + '</a></li>');
        });

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
                spinner = $('.spinner, .moj-auto-save__spinner', saveIcon);

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
                spinner = $('.spinner, .moj-auto-save__spinner', saveIcon),
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
                    complete: function (response) {
                        _.delay(endSaveIcon, 500, elem, response.status !== 200);
                    }
                });
            }
        }

        var quietSaveProgress = _.debounce(saveProgress, 5000);

        function saveAndUpdateTextLimits(editor, lengthCalculator) {
            quietSaveProgress(editor);

            var limit = editor.data('limit'),
                current = lengthCalculator(editor),
                messageHolder = $('#' + editor.attr('id') + '-countHolder'),
                messageTarget = $('#' + editor.attr('id') + '-count');

            if (limit && current > 0) {
                messageHolder.removeClass('govuk-visually-hidden');
                messageTarget.text(limit + ' recommended characters, you have used ' + current);
            } else {
                messageHolder.addClass('govuk-visually-hidden');
            }

        }


        // Analytics for 'What to include' links on SFR and PAROM1
        $('summary').click(function () {

            if (isSfr || isParom) {
                var details = $(this).parent(),
                    isOpen = details.attr('data-open') === 'true',
                    label = details.parent().find('.govuk-caption-xl').text() ||
                        details.parent().parent().find('legend').find('span').text() ||
                        details.parent().parent().find('legend').text() ||
                        details.prev('span:not(.govuk-hint)').text() ||
                        details.prev('span').prev('span').text()|| 'Unknown field';

              isOpen ? details.removeAttr('data-open') : details.attr('data-open', 'true')

              gtag('event', isOpen ? 'close' : 'open', {
                'event_category': (isSfr ? 'SFR' : 'PAROM1') + ' - What to include',
                'event_label': $('h1').text() + ' > ' + label.trim()
              })
            }
        });

        $('textarea').keyup(function () {
            var editor = $(this);
            saveAndUpdateTextLimits(editor, function () {
                return editor.val().length;
            });
        });

        $('input[type="text"], input[type="number"]').blur(function () {
            var editor = $(this);
            quietSaveProgress(editor);
        });

        $('input[type="radio"], input[type="checkbox"]').click(function () {
            var editor = $(this);
            quietSaveProgress(editor);
        });

        /**
         * Navigation items
         */
        $('.nav-item, .moj-subnav__link').click(function (e) {
            e.preventDefault();
            var target = $(this).data('target');
            if (target && !$(this).hasClass('active')) {
                $('#jumpNumber').val(target);
                $('form').submit();
            }
        });

        function trackViewDraft() {
            gtag('event', 'click', {
                'event_category': (isSfr ? 'SFR' : 'PAROM1') + ' - View draft',
                'event_label': 'Page: ' + $('h1').text()
            });
        }

        $('#draftReport').click(function (e) {

            trackViewDraft();

            var target = $(this).data('target');
            var form = $('form');
            $.ajax({
                type: 'POST',
                url: form.attr('action') + '/save',
                data: formWithZeroJumpNumber(form),
                complete: function (response) {
                    window.location = target;
                }
            });
        });

        $('.js-view-draft').click(function(e) {
            trackViewDraft();
        });

        /**
         * Ensure jumpNumber is cleared if next after clicking browser back button
         */
        $('#nextButton:not(.popup-launcher)').click(function () {

            if ($(this).text().indexOf('Submit') !== -1) {
                gtag('event', 'submit', {
                    'event_category': isSfr ? 'SFR' : 'PAROM1',
                    'event_label': 'Report submitted'
                });
            }

            $('#jumpNumber').val('');
        });

        $('#consideredQualityDiversity_yes').click(function () {
            if (typeof gtag === 'function') {
                gtag('event', 'short-format-equality-diversity-yes', {
                    'event_category': 'report',
                    'event_label': 'Short Format Report - Equality and Diversity - yes',
                    'value': 'yes'
                });
            }
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
                showHint(parent, child);
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

            // htmlunit no longer supports IE8 or conditionals so also check agent to rule out IE8
            // not needing once we upgrade away from HTMLUnit
            if (navigator.userAgent.indexOf('MSIE 8.0') === -1) {
                $('textarea:not(.classic)').each(function (i, elem) {
                    convertToEditor($(elem));
                });
            }
            // toggle editors back to visible previous hidden while quill initialises
            $('textarea').each(function (i, elem) {
                $(elem).css('visibility', 'visible');
            });

        }

        function replaceTextArea(textArea) {
            var attributesNotToBeCopied = ['name', 'placeholder', 'role'];
            var areaAttributes = attributesNotToBeCopied.reduce(function (accumulator, currentValue) {
                accumulator[currentValue] = textArea.attr(currentValue);
                return accumulator;
            }, {});
            var value = textArea.val();
            var editor = $('<div>' + value + '</div>');
            $.each(textArea[0].attributes, function (index, element) {
                if (attributesNotToBeCopied.indexOf(this.name) === -1) {
                    editor.attr(this.name, this.value);
                }
            });

            textArea.replaceWith(editor);
            editor.after('<input type="hidden" name="' + areaAttributes.name + '" value=""/>');
            editor.addClass('text-area-editor');
            return areaAttributes;

        }

        function convertToEditor(textArea) {
            var id = '#' + textArea.attr('id');
            var areaAttributes = replaceTextArea(textArea);

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
            });

            $(id).find('.ql-editor').attr('role', areaAttributes.role);

            function cleanHtml() {
                return '<!-- RICH_TEXT -->' + editor.root.innerHTML.replace(/<br>/gm, '<br/>');
            }

            function hasAnyText() {
                return editor.getText().trim();
            }

            function transferValueToInput() {
                if (hasAnyText()) {
                    $('input[name=\'' + areaAttributes.name + '\']').val(cleanHtml());
                } else {
                    $('input[name=\'' + areaAttributes.name + '\']').val('');
                }
            }

            function withModifier(text) {
                if (/Mac/i.test(navigator.platform)) {
                    return text;
                }
                return text.replace('⌘', 'Ctrl-').replace('⇧', 'Shift-');
            }


            editor.on('text-change', function (delta, oldDelta, source) {
                transferValueToInput();
            });

            transferValueToInput();

            // swap toolbar to below editor
            var toolbar = $(id).prev();
            toolbar.before($(id));

            // Include class to hide toolbar by default
            $(id).next().addClass('govuk-visually-hidden');

            function addTooltipsToToolbar() {
                toolbar.find('button').addClass('tooltip').addClass('moj-tooltip');
                var tips = [
                    { selector: '.ql-bold', tooltip: 'Bold (⌘B)' },
                    { selector: '.ql-italic', tooltip: 'Italic (⌘I)' },
                    { selector: '.ql-underline', tooltip: 'Underline (⌘U)' },
                    { selector: '.ql-align[value="justify"]', tooltip: 'Justify (⌘⇧J)' },
                    { selector: '.ql-align[value=""]', tooltip: 'Align Left (⌘⇧L)' },
                    { selector: '.ql-list[value="ordered"]', tooltip: 'Numbered List' },
                    { selector: '.ql-list[value="bullet"]', tooltip: 'Bulleted List' },
                    { selector: '.ql-clean', tooltip: 'Remove Formatting' }];

                function addTooltop(tip) {
                    toolbar.find(tip.selector + ' svg').after(withModifier('<span>' + tip.tooltip + '</span>'));
                }

                tips.forEach(addTooltop);
            }

            // show/hide toolbar with focus change
            editor.on('selection-change', function (range) {
                if (range) {
                    $(id).next().removeClass('govuk-visually-hidden');
                } else {
                    $(id).next().addClass('govuk-visually-hidden');
                }
            });

            editor.on('text-change', function () {
                saveAndUpdateTextLimits($(id), function () {
                    return editor.getText().trim().length;
                });
            });

            // add classes to reduce margins
            $(id).closest('.form-group').addClass('small-margin-bottom');
            $(id).closest('.form-group').addClass('govuk-!-margin-bottom-0');
            $(id).closest('.form-group').next('hr').addClass('small-margin-top');
            $(id).closest('.form-group').next('hr').addClass('govuk-!-margin-top-0');

            // remove tab key binding for editor, toolbar (and for IE11 svg)
            delete editor.getModule('keyboard').bindings[9];
            editor.getModule('keyboard').addBinding({
                key: 'J',
                shiftKey: true,
                shortKey: true
            }, function (range, context) {
                this.quill.format('align', 'justify');
            });
            editor.getModule('keyboard').addBinding({
                key: 'L',
                shiftKey: true,
                shortKey: true
            }, function (range, context) {
                this.quill.format('align', '');
            });
            toolbar.find(':button').attr('tabindex', '-1');
            toolbar.find('svg').attr('focusable', 'false');

            addTooltipsToToolbar();
        }


        var elementSelector = '.ql-editor,input[type!=hidden],textarea';
        $('form:first').find(elementSelector).first().focus();

        // disable back button as a browser back button else report data can easily be lost
        $(window).keydown(function (event) {
            if (event.which == '8'
                && event.target.type != 'text'
                && event.target.type != 'file'
                && event.target.type != 'number'
                && event.target.tagName != 'TEXTAREA'
                && event.target.className != 'ql-editor') {
                event.preventDefault();
                return false;
            }
        });

    });

    /**
     * Reveal or hide the other role section when 'Other' is chosen in the role drop down
     */
    $(document).on('change', '#role', function (e) {
        if ($('#role option:selected').text() === 'Other') {
            $('#roleother-section').removeClass('js-hidden');
        } else {
            $('#roleother-section').addClass('js-hidden');
        }
    });

    (function (global) {

        if (typeof (global) === 'undefined') {
            throw new Error('window is undefined');
        }

        var _hash = '!';
        var noBackPlease = function () {
            global.location.href += '#';

            global.setTimeout(function () {
                global.location.href += '!';
            }, 50);
        };

        global.onhashchange = function () {
            if (global.location.hash !== _hash) {
                global.location.hash = _hash;
            }
        };

        global.onload = function () {
            noBackPlease();
        };

    })(window);

})(window.jQuery);
