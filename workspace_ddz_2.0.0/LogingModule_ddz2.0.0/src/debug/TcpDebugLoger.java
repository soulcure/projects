/**
 * 
 */
package debug;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import android.util.Log;

/**
 * @author Jason
 * 
 */
public class TcpDebugLoger {
	public static boolean isDebug = false;

	public static boolean isAllSocket = true;

	private final String tag = "TcpDebugLoger";

	protected static TcpDebugLoger _instance = null;

	private Date _curDate = null;

	private Vector<TcpFilter> _tcpFilter = null;

	public static TcpDebugLoger getInstance() {
		if (_instance == null) {
			_instance = new TcpDebugLoger();
		}
		return _instance;
	}

	private TcpDebugLoger() {
		_curDate = new Date();
		_tcpFilter = new Vector<TcpDebugLoger.TcpFilter>();
	}

	public void logTcpReqInfo(final String str, final byte[] buf) {
		if (!isDebug) {
			return;
		}
		final int mdms[] = getDataMdm(buf);
		if (mdms[0] == 11) {
			return;
		}
		if (isAllSocket) {
			Log.e(tag, "---Req" + str + "-主=" + mdms[0] + "子=" + mdms[1] + ":" + DateFormat.getTimeInstance().format(_curDate));
			Log.e(tag, "---Req" + str + ":" + DateFormat.getTimeInstance().format(_curDate));
		} else {
			for (int i = 0; i < _tcpFilter.size(); i++) {
				TcpFilter filter = _tcpFilter.get(i);
				if (filter.isEquals(mdms[0], mdms[1])) { //
					_curDate.setTime(System.currentTimeMillis());
					Log.e(tag, "---Req" + str + "-主=" + mdms[0] + "子=" + mdms[1] + ":" + DateFormat.getTimeInstance().format(_curDate));
					Log.e(tag, "---Req" + str + ":" + DateFormat.getTimeInstance().format(_curDate));
				}
			}
		}
	}

	public static int[] getDataMdm(final byte[] buf) {
		if (buf == null || buf.length < 12) {
			return null;
		}
		int[] mdms = new int[2];
		mdms[0] = (buf[9] << 8) | buf[8];
		mdms[1] = (buf[11] << 8) | buf[10];
		return mdms;
	}

	public void logTcpRevInfo(final String str, final byte[] buf) {
		if (!isDebug) {
			return;
		}
		final int mdms[] = getDataMdm(buf);
		if (mdms[0] == 11) {
			return;
		}
		if (isAllSocket) {
			Log.e(tag, "+++Rev" + str + "-主=" + mdms[0] + "子=" + mdms[1] + ":" + DateFormat.getTimeInstance().format(_curDate));
			Log.e(tag, "+++Rev" + str + ":" + DateFormat.getTimeInstance().format(_curDate));
		} else {
			for (int i = 0; i < _tcpFilter.size(); i++) {
				TcpFilter filter = _tcpFilter.get(i);
				if (filter.isEquals(mdms[0], mdms[1])) { //
					_curDate.setTime(System.currentTimeMillis());
					Log.e(tag, "+++Rev" + str + "-主=" + mdms[0] + "子=" + mdms[1] + ":" + DateFormat.getTimeInstance().format(_curDate));
					Log.e(tag, "+++Rev" + str + ":" + DateFormat.getTimeInstance().format(_curDate));
				}
			}
		}
	}

	public void add(TcpFilter filter) {
		if (_tcpFilter == null) {
			return;
		}
		_tcpFilter.add(filter);
	}

	public static TcpFilter create(final int mCmd, final int sCmd) {
		TcpFilter filter = getInstance().new TcpFilter(mCmd, sCmd);
		return filter;
	}

	public class TcpFilter {
		protected int _mainCmd = 0;

		protected int _subCmd = 0;

		public TcpFilter(final int mCmd, final int sCmd) {
			_mainCmd = mCmd;
			_subCmd = sCmd;
		}

		public boolean isEquals(final int mCmd, final int sCmd) {
			return _mainCmd == mCmd && _subCmd == sCmd;
		}
	}
}
