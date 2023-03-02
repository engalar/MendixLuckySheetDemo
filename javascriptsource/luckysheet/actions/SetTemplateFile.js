// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
import { Big } from "big.js";

// BEGIN EXTRA CODE
// END EXTRA CODE

/**
 * @param {string} widgetid
 * @param {string} template
 * @returns {Promise.<void>}
 */
export async function SetTemplateFile(widgetid, template) {
	// BEGIN USER CODE
	const res = await fetch(`file?guid=${template}`);
	const file = await res.arrayBuffer();

	document.querySelector(`#${widgetid} iframe`).contentWindow 
		&& document.querySelector(`#${widgetid} iframe`).contentWindow.postMessage({
			mx_luckysheet: {
				id: 'setup', 
				data: file, 
				lang: mx.session.sessionData.locale.code.split('_')[0],
				widgetid
			}
		}, '*');
	// END USER CODE
}
