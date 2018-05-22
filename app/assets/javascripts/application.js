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
            var saveIcon = $('#' + elem.attr('id') + '_save'),
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
            var saveIcon = $('#' + elem.attr('id') + '_save'),
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
        /**
         * Textarea elements
         */
         $('textarea').keyup(function () {
            quietSaveProgress($(this));

            var textArea = $(this),
                limit = textArea.data('limit'),
                current = textArea.val().length,
                messageHolder = $('#' + textArea.attr('id') + '-countHolder'),
                messageTarget = $('#' + textArea.attr('id') + '-count');

            if (limit && current > 0) {
                messageHolder.removeClass('js-hidden');
                messageTarget.text(limit + ' recommended characters, you have used ' + current);
            } else {
                messageHolder.addClass('js-hidden');
            }

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

        var popupLaunched

        $('.popup-launcher').click(function (e) {
            e.preventDefault();
            if (popupLaunched) {
                var onBehalfOfUser = encodeURIComponent($('#onBehalfOfUser').val())
                var documentId = encodeURIComponent($('#documentId').val())
                var url = $('form').attr('action') + '?documentId=' + documentId + '&onBehalfOfUser=' + onBehalfOfUser + '&continue=true';
                openPopup(url)
            } else {
                openPopup('about:blank')
                $('form').attr("target", "reportpopup")
                $('form').submit()
                popupLaunched = true
            }
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
            autosize(document.querySelectorAll('textarea'));

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
        }

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
