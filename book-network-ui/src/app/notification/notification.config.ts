import { RxStompConfig } from '@stomp/rx-stomp';

const WS_ROOT_URL = 'ws://127.0.0.1:8088/api/v1/ws';

export const notificationConfig: RxStompConfig = {
  // Which server?
  brokerURL: WS_ROOT_URL,

  // Headers
  // Typical keys: login, passcode, host
  connectHeaders: {
    /*   login: 'guest',
     passcode: 'guest',*/
  },


  // How often to heartbeat?
  // Interval in milliseconds, set to 0 to disable
  heartbeatIncoming: 0, // Typical value 0 - disabled
  heartbeatOutgoing: 20000, // Typical value 20000 - every 20 seconds

  // Wait in milliseconds before attempting auto reconnect
  // Set to 0 to disable
  // Typical value 500 (500 milliseconds)
  reconnectDelay: 2000,
  logRawCommunication: true,

  // Will log diagnostics on console
  // It can be quite verbose, not recommended in production
  // Skip this key to stop logging to console
  debug: (msg: string): void => {
    console.log(new Date(), msg);
  },
};
