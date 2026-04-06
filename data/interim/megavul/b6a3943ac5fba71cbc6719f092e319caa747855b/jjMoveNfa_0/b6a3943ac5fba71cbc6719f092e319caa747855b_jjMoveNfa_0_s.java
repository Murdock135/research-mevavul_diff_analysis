class jjMoveNfa_0 {
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 7;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 6:
                  if ((0xffffffe7ffffffffL & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                     jjCheckNAddStates(0, 3);
                  }
                  else if ((0x1800000000L & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                  }
                  if (curChar == 35)
                     jjCheckNAdd(4);
                  else if (curChar == 36)
                     jjCheckNAdd(4);
                  break;
               case 0:
               case 4:
                  if ((0xffffffe7ffffffffL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 3);
                  break;
               case 2:
                  if ((0x1800000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 3);
                  break;
               case 3:
                  if (curChar == 36)
                     jjCheckNAdd(4);
                  break;
               case 5:
                  if (curChar == 35)
                     jjCheckNAdd(4);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 6:
                  if ((0xffffffffefffffffL & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                     jjCheckNAddStates(0, 3);
                  }
                  else if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 0:
                  if ((0xffffffffefffffffL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 3);
                  break;
               case 1:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 2:
                  if (curChar != 92)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 3);
                  break;
               case 4:
                  if ((0xf7ffffffffffffffL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 3);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 6:
               case 0:
               case 4:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 3);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 7 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
}
