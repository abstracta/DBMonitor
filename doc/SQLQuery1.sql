SELECT	a.[MESSAGE_ID]
		,b.[SEND_DATE] - a.[RECEIVED_DATE] as 'DelayInternal'
		,b.[RESPONSE_DATE] - b.[SEND_DATE] as 'DelayExternal'
FROM	[mirthdb].[dbo].[D_MM1] a
		,[mirthdb].[dbo].[D_MM1] b
WHERE	a.MESSAGE_ID = 2 
		and a.MESSAGE_ID = b.MESSAGE_ID 
		and a.[STATUS] = 'T' 
		and b.[STATUS] = 'S'


SELECT	a.[MESSAGE_ID]
		,a.[RECEIVED_DATE], b.RECEIVED_DATE, b.SEND_DATE, b.RESPONSE_DATE
		,b.[SEND_DATE] - a.[RECEIVED_DATE] as 'DelayInterno'
		,b.[RESPONSE_DATE] - b.[SEND_DATE] as 'DelayExterno'

FROM	[mirthdb].[dbo].[D_MM1] a, 
		[mirthdb].[dbo].[D_MM1] b
WHERE	a.MESSAGE_ID > 2 and a.message_id < 8 
		and a.MESSAGE_ID = b.MESSAGE_ID 
		and a.[STATUS] = 'T' 
		and b.[STATUS] = 'S'

select * 
from [mirthdb].[dbo].[D_MM1] 
where MESSAGE_ID = 2;

SELECT 
      a.[NAME]
	  ,b.[LOCAL_CHANNEL_ID]
FROM 
	[mirthdb].[dbo].[CHANNEL] a
	,[mirthdb].[dbo].[D_CHANNELS] b
WHERE 
	a.ID = b.CHANNEL_ID;


/*
select	MESSAGE_ID, RECEIVED_DATE, SEND_DATE, RESPONSE_DATE 
from	[mirthdb].[dbo].[D_MM1]
where	MESSAGE_ID > 2;
*/

/*
SELECT	a.[MESSAGE_ID], 
		a.[RECEIVED_DATE] as ReceiveT, 
		b.[RECEIVED_DATE] as ReceiveS,
		b.[SEND_DATE] as SendS,
		b.[RESPONSE_DATE] as ResponseS
FROM	[mirthdb].[dbo].[D_MM1] a, 
		[mirthdb].[dbo].[D_MM1] b
WHERE	a.MESSAGE_ID > 2 
		and a.MESSAGE_ID = b.MESSAGE_ID 
		and a.[STATUS] = 'T' 
		and b.[STATUS] = 'S'

select MESSAGE_ID, RECEIVED_DATE, [STATUS], SEND_DATE, RESPONSE_DATE,ERROR_CODE, CHAIN_ID, ORDER_ID 
from [mirthdb].[dbo].[D_MM1]
where MESSAGE_ID > 2;

select * 
from [mirthdb].[dbo].[D_MM1] 
where MESSAGE_ID = 2;

*/