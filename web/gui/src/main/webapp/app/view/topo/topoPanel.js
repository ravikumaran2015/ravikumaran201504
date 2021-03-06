/*
 * Copyright 2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 ONOS GUI -- Topology Panel Module.
 Defines functions for manipulating the summary, detail, and instance panels.
 */

(function () {
    'use strict';

    // injected refs
    var $log, fs, ps, gs, flash, wss;

    // constants
    var pCls = 'topo-p',
        idSum = 'topo-p-summary',
        idDet = 'topo-p-detail',
        panelOpts = {
            width: 260
        };

    // panels
    var summaryPanel,
        detailPanel;

    // internal state
    var useDetails = true,      // should we show details if we have 'em?
        haveDetails = false;    // do we have details that we could show?

    // === -----------------------------------------------------
    // Utility functions

    function addSep(tbody) {
        tbody.append('tr').append('td').attr('colspan', 2).append('hr');
    }

    function addProp(tbody, label, value) {
        var tr = tbody.append('tr'),
            lab = label.replace(/_/g, ' ');

        function addCell(cls, txt) {
            tr.append('td').attr('class', cls).html(txt);
        }
        addCell('label', lab + ' :');
        addCell('value', value);
    }

    function listProps(tbody, data) {
        data.propOrder.forEach(function(p) {
            if (p === '-') {
                addSep(tbody);
            } else {
                addProp(tbody, p, data.props[p]);
            }
        });
    }

    function dpa(x) {
        return detailPanel.append(x);
    }

    function spa(x) {
        return summaryPanel.append(x);
    }

    // === -----------------------------------------------------
    //  Functions for populating the summary panel

    function populateSummary(data) {
        summaryPanel.empty();

        var svg = spa('svg'),
            title = spa('h2'),
            table = spa('table'),
            tbody = table.append('tbody');

        gs.addGlyph(svg, 'node', 40);
        gs.addGlyph(svg, 'bird', 24, true, [8,12]);

        title.text(data.id);
        listProps(tbody, data);
    }

    // === -----------------------------------------------------
    //  Functions for populating the detail panel

    function displaySingle(data) {
        detailPanel.empty();

        var svg = dpa('svg'),
            title = dpa('h2'),
            table = dpa('table'),
            tbody = table.append('tbody');

        gs.addGlyph(svg, (data.type || 'unknown'), 40);
        title.text(data.id);
        listProps(tbody, data);
        dpa('hr');
    }

    function displayMulti(ids) {
        detailPanel.empty();

        var title = dpa('h3'),
            table = dpa('table'),
            tbody = table.append('tbody');

        title.text('Selected Nodes');
        ids.forEach(function (d, i) {
            addProp(tbody, i+1, d);
        });
        dpa('hr');
    }

    function addAction(text, cb) {
        dpa('div')
            .classed('actionBtn', true)
            .text(text)
            .on('click', cb);
    }

    var friendlyIndex = {
        device: 1,
        host: 0
    };

    function friendly(d) {
        var i = friendlyIndex[d.class] || 0;
        return (d.labels && d.labels[i]) || '';
    }

    function linkSummary(d) {
        var o = d && d.online ? 'online' : 'offline';
        return d ? d.type + ' / ' + o : '-';
    }

    // provided to change presentation of internal type name
    var linkTypePres = {
        hostLink: 'edge link'
    };

    function linkType(d) {
        return linkTypePres[d.type()] || d.type();
    }

    var coreOrder = [
            'Type', '-',
            'A_type', 'A_id', 'A_label', 'A_port', '-',
            'B_type', 'B_id', 'B_label', 'B_port', '-'
        ],
        edgeOrder = [
            'Type', '-',
            'A_type', 'A_id', 'A_label', '-',
            'B_type', 'B_id', 'B_label', 'B_port'
        ];

    function displayLink(data) {
        detailPanel.empty();

        var svg = dpa('svg'),
            title = dpa('h2'),
            table = dpa('table'),
            tbody = table.append('tbody'),
            edgeLink = data.type() === 'hostLink',
            order = edgeLink ? edgeOrder : coreOrder;

        gs.addGlyph(svg, 'ports', 40);
        title.text('Link');


        listProps(tbody, {
            propOrder: order,
            props: {
                Type: linkType(data),

                A_type: data.source.class,
                A_id: data.source.id,
                A_label: friendly(data.source),
                A_port: data.srcPort,

                B_type: data.target.class,
                B_id: data.target.id,
                B_label: friendly(data.target),
                B_port: data.tgtPort
            }
        });

        if (!edgeLink) {
            addProp(tbody, 'A &rarr; B', linkSummary(data.fromSource));
            addProp(tbody, 'B &rarr; A', linkSummary(data.fromTarget));
        }
    }

    function displayNothing() {
        haveDetails = false;
        hideDetailPanel();
    }

    function displaySomething() {
        haveDetails = true;
        if (useDetails) {
            showDetailPanel();
        }
    }

    // === -----------------------------------------------------
    //  Event Handlers

    function showSummary(data) {
        populateSummary(data);
        showSummaryPanel();
    }

    function toggleSummary(x) {
        var kev = (x === 'keyev'),
            on = kev ? !summaryPanel.isVisible() : !!x,
            verb = on ? 'Show' : 'Hide';

        if (on) {
            // ask server to start sending summary data.
            wss.sendEvent('requestSummary');
            // note: the summary panel will appear, once data arrives
        } else {
            hideSummaryPanel();
        }
        flash.flash(verb + ' summary panel');
        return on;
    }

    // === -----------------------------------------------------
    // === LOGIC For showing/hiding summary and detail panels...

    function showSummaryPanel() {
        if (detailPanel.isVisible()) {
            detailPanel.down(summaryPanel.show);
        } else {
            summaryPanel.show();
        }
    }

    function hideSummaryPanel() {
        // instruct server to stop sending summary data
        wss.sendEvent("cancelSummary");
        summaryPanel.hide(detailPanel.up);
    }

    function showDetailPanel() {
        if (summaryPanel.isVisible()) {
            detailPanel.down(detailPanel.show);
        } else {
            detailPanel.up(detailPanel.show);
        }
    }

    function hideDetailPanel() {
        detailPanel.hide();
    }

    // ==========================

    function noop () {}

    function augmentDetailPanel() {
        var dp = detailPanel;
        dp.ypos = { up: 64, down: 320, current: 320};

        dp._move = function (y, cb) {
            var endCb = fs.isF(cb) || noop,
                yp = dp.ypos;
            if (yp.current !== y) {
                yp.current = y;
                dp.el().transition().duration(300)
                    .each('end', endCb)
                    .style('top', yp.current + 'px');
            } else {
                endCb();
            }
        };

        dp.down = function (cb) { dp._move(dp.ypos.down, cb); };
        dp.up = function (cb) { dp._move(dp.ypos.up, cb); };
    }

    function toggleDetails(x) {
        var kev = (x === 'keyev'),
            verb;

        useDetails = kev ? !useDetails : !!x;
        verb = useDetails ? 'Enable' : 'Disable';

        if (useDetails) {
            if (haveDetails) {
                showDetailPanel();
            }
        } else {
            hideDetailPanel();
        }
        flash.flash(verb + ' details panel');
        return useDetails;
    }

    // ==========================

    function initPanels() {
        summaryPanel = ps.createPanel(idSum, panelOpts);
        detailPanel = ps.createPanel(idDet, panelOpts);

        summaryPanel.classed(pCls, true);
        detailPanel.classed(pCls, true);

        augmentDetailPanel();
    }

    function destroyPanels() {
        ps.destroyPanel(idSum);
        ps.destroyPanel(idDet);
        summaryPanel = detailPanel = null;
    }

    // ==========================

    angular.module('ovTopo')
    .factory('TopoPanelService',
        ['$log', 'FnService', 'PanelService', 'GlyphService',
            'FlashService', 'WebSocketService',

        function (_$log_, _fs_, _ps_, _gs_, _flash_, _wss_) {
            $log = _$log_;
            fs = _fs_;
            ps = _ps_;
            gs = _gs_;
            flash = _flash_;
            wss = _wss_;

            return {
                initPanels: initPanels,
                destroyPanels: destroyPanels,

                showSummary: showSummary,
                toggleSummary: toggleSummary,

                toggleDetails: toggleDetails,
                displaySingle: displaySingle,
                displayMulti: displayMulti,
                addAction: addAction,
                displayLink: displayLink,
                displayNothing: displayNothing,
                displaySomething: displaySomething,

                hideSummaryPanel: hideSummaryPanel,

                detailVisible: function () { return detailPanel.isVisible(); },
                summaryVisible: function () { return summaryPanel.isVisible(); }
            };
        }]);
}());
